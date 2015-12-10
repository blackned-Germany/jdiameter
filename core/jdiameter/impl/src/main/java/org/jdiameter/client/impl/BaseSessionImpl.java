/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.client.impl;


import org.jdiameter.api.*;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IEventListener;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;

import static org.jdiameter.client.impl.helpers.Parameters.MessageTimeOut;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation for {@link BaseSession}.
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public abstract class BaseSessionImpl implements BaseSession {

  protected final long creationTime = System.currentTimeMillis();
  protected long lastAccessedTime = creationTime;
  protected boolean isValid = true;
  protected String sessionId;

  protected transient IContainer container;
  protected transient IMessageParser parser;
  protected NetworkReqListener reqListener;

  public long getCreationTime() {
    return creationTime;
  }

  public long getLastAccessedTime() {
    return lastAccessedTime;
  }

  public boolean isValid() {
    return isValid;
  }

  public String getSessionId() {
    return sessionId;
  }

  /* (non-Javadoc)
   * @see BaseSession#isAppSession()
   */
  public boolean isAppSession() {
    return false;
  }

  /* (non-Javadoc)
   * @see BaseSession#isReplicable()
   */
  public boolean isReplicable() {
    return false;
  }

  protected void genericSend(Message message, EventListener listener) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (isValid) {
      long timeOut = container.getConfiguration().getLongValue(MessageTimeOut.ordinal(), (Long) MessageTimeOut.defValue());
      genericSend(message, listener, timeOut, TimeUnit.MILLISECONDS);
    }
    else {
      throw new IllegalDiameterStateException("Session already released");
    }
  }

  protected void genericSend(Message aMessage, EventListener listener, long timeout, TimeUnit timeUnit) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (isValid) {
      lastAccessedTime = System.currentTimeMillis();

      IMessage message = (IMessage) aMessage;
      IEventListener localListener = createListenerWrapper(listener);
      if (message.isRequest()) {
        message.setListener(localListener);

        // Auto set system avps
        if (message.getAvps().getAvpByIndex(0).getCode() != Avp.SESSION_ID && sessionId != null) {
          // Just to make sure it doesn't get duplicated 
          message.getAvps().removeAvp(Avp.SESSION_ID);
          message.getAvps().insertAvp(0, Avp.SESSION_ID, sessionId, true, false, false);
        }
      }

      //Add Origin-Host/Realm AVPs if not present
      MessageUtility.addOriginAvps(aMessage, container.getMetaData());

      if (message.getState() != IMessage.STATE_NOT_SENT && message.getState() != IMessage.STATE_ANSWERED) {
        throw new IllegalDiameterStateException("Illegal state");
      }

      message.createTimer(container.getScheduledFacility(), timeout, timeUnit);
      try {
        container.sendMessage(message);
      }
      catch(RouteException e) {
        message.clearTimer();
        throw e;
      }
      catch (Exception e) {
        message.clearTimer();
        throw new InternalException(e);
      }
    }
    else {
      throw new IllegalDiameterStateException("Session already released");
    }
  }

  @SuppressWarnings("unchecked")
  protected IEventListener createListenerWrapper(final EventListener listener) {
    return listener == null ? null : new MyEventListener(this, listener);
  }

  public Future<Message> send(final Message message) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    MyFuture future = new MyFuture();
    future.send(message);
    return future;
  }

  public Future<Message> send(Message message, long timeOut, TimeUnit timeUnit) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    MyFuture future = new MyFuture();
    future.send(message, timeOut, timeUnit);
    return future;
  }

  private class MyFuture implements Future<Message> {

    private boolean canceled;
    private boolean done;
    private boolean timeOut;
    private Lock lock = new ReentrantLock();
    private CountDownLatch block = new CountDownLatch(1);
    private Message result;

    public boolean cancel(boolean mayInterruptIfRunning) {
      lock.lock();
      try {
        canceled = true;
        done = false;
        block.countDown();
      }
      finally {
        lock.unlock();
      }

      return true;
    }

    public boolean isCancelled() {
      return canceled;
    }

    public boolean isDone() {
      return done;
    }

    public Message get() throws InterruptedException, ExecutionException {
      try {
        block.await();
      }
      catch (Exception e) {
        throw new ExecutionException(e);
      }

      Message rc = canceled ? null : result;
      result = null;
      return rc;
    }

    public Message get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      try {
        block.await(timeout, unit);
      }
      catch (Exception e) {
        throw new ExecutionException(e);
      }

      if (timeOut) {
        throw new TimeoutException();
      }

      Message rc = canceled ? null : result;
      result = null;
      return rc;
    }

    private IEventListener createListener() {
      return new IEventListener() {

        public void setValid(boolean value) {
        }

        public boolean isValid() {
          return !canceled;
        }

        public void receivedSuccessMessage(Request r, Answer a) {
          lock.lock();
          try {
            if (!canceled) {
              result = a;
              canceled = false;
              done = true;
            }
            block.countDown();
          }
          finally {
            lock.unlock();
          }
        }

        public void timeoutExpired(Request message) {
          lock.lock();
          try {
            if (!canceled) {
              done = true;
              timeOut = true;
            }
            block.countDown();
          }
          finally {
            lock.unlock();
          }
        }
      };
    }

    public void send(Message message) throws RouteException, OverloadException, IllegalDiameterStateException, InternalException {
      genericSend(message, createListener());
    }

    public void send(Message message, long timeOut, TimeUnit timeUnit) throws RouteException, OverloadException, IllegalDiameterStateException, InternalException {
      genericSend(message, createListener(), timeOut, timeUnit);
    }
  }

  /**
   * Appends an *-Application-Id AVP to the message, if none is present already.
   * 
   * @param appId the application-id value
   * @param m the message to append the *-Application-Id
   */
  protected void appendAppId(ApplicationId appId, Message m) {
    if (appId == null) {
      return;
    }

    // check if any application-id avp is already present.
    // we could use m.getApplicationIdAvps().size() > 0 but this should spare a few cpu cycles
    for(Avp avp : m.getAvps()) {
      int code = avp.getCode();
      if(code == Avp.ACCT_APPLICATION_ID || code == Avp.AUTH_APPLICATION_ID || code == Avp.VENDOR_SPECIFIC_APPLICATION_ID) {
        return;
      }
    }

    if (appId.getVendorId() == 0) {
      if (appId.getAcctAppId() != 0) {
        m.getAvps().addAvp(Avp.ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true);
      }
      if (appId.getAuthAppId() != 0) {
        m.getAvps().addAvp(Avp.AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true);
      }
    }
    else {
      AvpSet avp = m.getAvps().addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, true, false);
      avp.addAvp(Avp.VENDOR_ID, appId.getVendorId(), true, false, true);
      if (appId.getAuthAppId() != 0) {
        avp.addAvp(Avp.AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true);
      }
      if (appId.getAcctAppId() != 0) {
        avp.addAvp(Avp.ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true);
      }
    }
  }

  protected long getAppId(ApplicationId appId) {
    if (appId == null) {
      return 0;
    }
    // if (appId.getVendorId() == 0) {
    if (appId.getAcctAppId() != 0) {
      return appId.getAcctAppId();
    }
    if (appId.getAuthAppId() != 0) {
      return appId.getAuthAppId();
    }
    // }
    return appId.getVendorId();
  }
}

class MyEventListener implements IEventListener {

  BaseSessionImpl session;
  EventListener listener;
  boolean isValid = true;

  public MyEventListener(BaseSessionImpl session, EventListener listener) {
    this.session = session;
    this.listener = listener;
  }

  public void setValid(boolean value) {
    isValid = value;
    if (!isValid) {
      session = null;
      listener = null;
    }
  }

  public boolean isValid() {
    return isValid;
  }

  @SuppressWarnings("unchecked")
  public void receivedSuccessMessage(Request request, Answer answer) {
    if (isValid) {
      session.lastAccessedTime = System.currentTimeMillis();
      listener.receivedSuccessMessage(request, answer);
    }
  }

  @SuppressWarnings("unchecked")
  public void timeoutExpired(Request message) {
    if (isValid) {
      session.lastAccessedTime = System.currentTimeMillis();
      listener.timeoutExpired(message);
    }
  }

  public int hashCode() {
    return listener == null ? 0 : listener.hashCode();
  }

  public boolean equals(Object obj) {
    return listener != null && listener.equals(obj);
  }

  public String toString() {
    return listener == null ? "null" : listener.toString();
  }
}
