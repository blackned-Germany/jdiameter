/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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

package org.jdiameter.common.impl.app.rx;

import java.util.concurrent.ScheduledFuture;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;

//import ReAuthAnswer;
//import ReAuthRequest;
import org.jdiameter.api.rx.events.RxReAuthAnswer;
import org.jdiameter.api.rx.events.RxReAuthRequest;


//import AbortSessionAnswer;
//import AbortSessionRequest;
import org.jdiameter.api.rx.events.RxAbortSessionAnswer;
import org.jdiameter.api.rx.events.RxAbortSessionRequest;



//import SessionTermAnswer;
//import SessionTermRequest;
import org.jdiameter.api.rx.events.RxSessionTermAnswer;
import org.jdiameter.api.rx.events.RxSessionTermRequest;

import org.jdiameter.api.rx.ClientRxSession;
import org.jdiameter.api.rx.ClientRxSessionListener;
import org.jdiameter.api.rx.ServerRxSession;
import org.jdiameter.api.rx.ServerRxSessionListener;
import org.jdiameter.api.rx.events.RxAAAnswer;
import org.jdiameter.api.rx.events.RxAARequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.rx.ClientRxSessionImpl;

import org.jdiameter.client.impl.app.rx.IClientRxSessionData;
import org.jdiameter.common.api.app.IAppSessionDataFactory;

import org.jdiameter.common.api.app.rx.IRxSessionData;

import org.jdiameter.common.api.app.rx.IClientRxSessionContext;
import org.jdiameter.common.api.app.rx.IRxMessageFactory;
import org.jdiameter.common.api.app.rx.IRxSessionFactory;
import org.jdiameter.common.api.app.rx.IServerRxSessionContext;
import org.jdiameter.common.api.data.ISessionDatasource;

//import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
//import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;

//import org.jdiameter.common.impl.app.auth.AbortSessionAnswerImpl;
//import org.jdiameter.common.impl.app.auth.AbortSessionRequestImpl;

//import org.jdiameter.common.impl.app.auth.SessionTermAnswerImpl;
//import org.jdiameter.common.impl.app.auth.SessionTermRequestImpl;


import org.jdiameter.server.impl.app.rx.IServerRxSessionData;
import org.jdiameter.server.impl.app.rx.ServerRxSessionImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Diameter Rx Session Factory implementation.
 *
 * @author <a href="mailto:richard.good@smilecoms.com"> Richard Good </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RxSessionFactoryImpl implements IRxSessionFactory, ClientRxSessionListener, ServerRxSessionListener, StateChangeListener<AppSession>,
IRxMessageFactory, IServerRxSessionContext, IClientRxSessionContext {

  // Message timeout value (in milliseconds)
  protected int defaultDirectDebitingFailureHandling = 0;
  protected int defaultAAFailureHandling = 0;
  // its seconds
  protected long defaultValidityTime = 60;
  protected long defaultTxTimerValue = 30;
  // local not replicated listeners:
  protected ClientRxSessionListener clientSessionListener;
  protected ServerRxSessionListener serverSessionListener;
  protected StateChangeListener<AppSession> stateListener;
  protected IServerRxSessionContext serverContextListener;
  protected IClientRxSessionContext clientContextListener;
  protected IRxMessageFactory messageFactory;
  protected Logger logger = LoggerFactory.getLogger(RxSessionFactoryImpl.class);
  protected ISessionDatasource iss;
  protected ISessionFactory sessionFactory = null;
  protected IAppSessionDataFactory<IRxSessionData> sessionDataFactory;

  public RxSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<IRxSessionData>) this.iss.getDataFactory(IRxSessionData.class);

    if(this.sessionDataFactory == null) {
      logger.debug("Initialized Rx SessionDataFactory is null");
    }
  }

  public RxSessionFactoryImpl(SessionFactory sessionFactory, int defaultDirectDebitingFailureHandling, int defaultAAFailureHandling,
      long defaultValidityTime, long defaultTxTimerValue) {
    this(sessionFactory);

    this.defaultDirectDebitingFailureHandling = defaultDirectDebitingFailureHandling;
    this.defaultAAFailureHandling = defaultAAFailureHandling;
    this.defaultValidityTime = defaultValidityTime;
    this.defaultTxTimerValue = defaultTxTimerValue;
  }

  /**
   * @return the clientSessionListener
   */
  public ClientRxSessionListener getClientSessionListener() {
    return clientSessionListener != null ? clientSessionListener : this; 
  }

  /**
   * @param clientSessionListener
   *          the clientSessionListener to set
   */
  public void setClientSessionListener(final ClientRxSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the serverSessionListener
   */
  public ServerRxSessionListener getServerSessionListener() {
    return serverSessionListener != null ? serverSessionListener : this;
  }

  /**
   * @param serverSessionListener
   *          the serverSessionListener to set
   */
  public void setServerSessionListener(ServerRxSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the serverContextListener
   */
  public IServerRxSessionContext getServerContextListener() {
    return serverContextListener != null ? serverContextListener : this;
  }

  /**
   * @param serverContextListener
   *          the serverContextListener to set
   */
  public void setServerContextListener(IServerRxSessionContext serverContextListener) {
    this.serverContextListener = serverContextListener;
  }

  /**
   * @return the clientContextListener
   */
  public IClientRxSessionContext getClientContextListener() {
    return clientContextListener != null ? clientContextListener : this;
  }

  /**
   * @return the messageFactory
   */
  public IRxMessageFactory getMessageFactory() {
    return messageFactory != null ? messageFactory : this;
  }

  /**
   * @param messageFactory
   *          the messageFactory to set
   */
  public void setMessageFactory(final IRxMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @param clientContextListener
   *          the clientContextListener to set
   */
  public void setClientContextListener(IClientRxSessionContext clientContextListener) {
    this.clientContextListener = clientContextListener;
  }

  /**
   * @return the sessionFactory
   */
  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /**
   * @param sessionFactory
   *          the sessionFactory to set
   */
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = (ISessionFactory) sessionFactory;
  }

  /**
   * @return the stateListener
   */
  public StateChangeListener<AppSession> getStateListener() {
    return stateListener != null ? stateListener : this;
  }

  /**
   * @param stateListener
   *          the stateListener to set
   */
  public void setStateListener(StateChangeListener<AppSession> stateListener) {
    this.stateListener = stateListener;
  }

  @Override
  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    if (sessionId == null) {
      throw new IllegalArgumentException("SessionId must not be null");
    }
    if(!this.iss.exists(sessionId)) {
      return null;
    }
    AppSession appSession = null;
    try {
      if (aClass == ClientRxSession.class) {
        IClientRxSessionData sessionData =  (IClientRxSessionData) this.sessionDataFactory.getAppSessionData(ClientRxSession.class, sessionId);
        ClientRxSessionImpl clientSession = new ClientRxSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(), this.getClientContextListener(), this.getStateListener());

        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else if (aClass == ServerRxSession.class) {
        IServerRxSessionData sessionData =  (IServerRxSessionData) this.sessionDataFactory.getAppSessionData(ServerRxSession.class, sessionId);
        ServerRxSessionImpl serverSession = new ServerRxSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(), this.getServerContextListener(), this.getStateListener());

        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientRxSession.class + "," + ServerRxSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Rx Session.", e);
    }

    return appSession;
  }


  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    AppSession appSession = null;
    try {
      // FIXME:
      if (aClass == ClientRxSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IClientRxSessionData sessionData =  (IClientRxSessionData) this.sessionDataFactory.getAppSessionData(ClientRxSession.class, sessionId);
        ClientRxSessionImpl clientSession = new ClientRxSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getClientSessionListener(), this.getClientContextListener(), this.getStateListener());
        // this goes first!
        iss.addSession(clientSession);
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else if (aClass == ServerRxSession.class) {
        if (sessionId == null) {
          if (args != null && args.length > 0 && args[0] instanceof Request) {
            Request request = (Request) args[0];
            sessionId = request.getSessionId();
          }
          else {
            sessionId = this.sessionFactory.getSessionId();
          }
        }
        IServerRxSessionData sessionData =  (IServerRxSessionData) this.sessionDataFactory.getAppSessionData(ServerRxSession.class, sessionId);
        ServerRxSessionImpl serverSession = new ServerRxSessionImpl(sessionData, this.getMessageFactory(), sessionFactory, this.getServerSessionListener(), this.getServerContextListener(), this.getStateListener());
        iss.addSession(serverSession);
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ClientRxSession.class + "," + ServerRxSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new Rx Session.", e);
    }

    return appSession;
  }


  // Default implementation of methods so there are no exception!

  // Message Handlers --------------------------------------------------------
  public void doAARequest(ServerRxSession session, RxAARequest request) throws InternalException {
  }

  public void doAAAnswer(ClientRxSession session, RxAARequest request, RxAAAnswer answer) throws InternalException {
  }

  public void doReAuthRequest(ClientRxSession session, RxReAuthRequest request) throws InternalException {
  }

  public void doReAuthAnswer(ServerRxSession session, RxReAuthRequest request, RxReAuthAnswer answer) throws InternalException {
  }

  public void doAbortSessionRequest(ClientRxSession session, RxAbortSessionRequest request) throws InternalException {
  }

  public void doAbortSessionAnswer(ServerRxSession session, RxAbortSessionRequest request, RxAbortSessionAnswer answer) throws InternalException {
  }

  public void doSessionTermRequest(ServerRxSession session, RxSessionTermRequest request) throws InternalException {
  }

  public void doSessionTermAnswer(ClientRxSession session, RxSessionTermRequest request, RxSessionTermAnswer answer) throws InternalException {
  }

  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {
  }

  // Message Factory Methods -------------------------------------------------
  public RxAAAnswer createAAAnswer(Answer answer) {
    return new RxAAAnswerImpl(answer);
  }

  public RxAARequest createAARequest(Request req) {
    return new RxAARequestImpl(req);
  }

  public RxReAuthAnswer createReAuthAnswer(Answer answer) {
    return new RxReAuthAnswerImpl(answer);
  }

  public RxReAuthRequest createReAuthRequest(Request req) {
    return new RxReAuthRequestImpl(req);
  }

  public RxSessionTermAnswer createSessionTermAnswer(Answer answer) {
    return new RxSessionTermAnswerImpl(answer);
  }

  public RxSessionTermRequest createSessionTermRequest(Request req) {
    return new RxSessionTermRequestImpl(req);
  }

  public RxAbortSessionAnswer createAbortSessionAnswer(Answer answer) {
    return new RxAbortSessionAnswerImpl(answer);
  }

  public RxAbortSessionRequest createAbortSessionRequest(Request req) {
    return new RxAbortSessionRequestImpl(req);
  }

  // Context Methods ----------------------------------------------------------
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter Rx SessionFactory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  /*
   * (non-Javadoc)
   *
   * @see StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter Rx SessionFactory :: stateChanged :: source[{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
  }

  // FIXME: add ctx methods proxy calls!
  public void sessionSupervisionTimerExpired(ServerRxSession session) {
    // this.resourceAdaptor.sessionDestroyed(session.getSessions().get(0).getSessionId(), session);
    session.release();
  }

  public void sessionSupervisionTimerReStarted(ServerRxSession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  public void sessionSupervisionTimerStarted(ServerRxSession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  public void sessionSupervisionTimerStopped(ServerRxSession session, ScheduledFuture future) {
    // TODO Complete this method.
  }

  public void timeoutExpired(Request request) {
    // FIXME What should we do when there's a timeout?
  }

  public void denyAccessOnDeliverFailure(ClientRxSession clientRxSessionImpl, Message request) {
    // TODO Complete this method.
  }

  public void denyAccessOnFailureMessage(ClientRxSession clientRxSessionImpl) {
    // TODO Complete this method.
  }

  public void denyAccessOnTxExpire(ClientRxSession clientRxSessionImpl) {
    // this.resourceAdaptor.sessionDestroyed(clientRxSessionImpl.getSessions().get(0).getSessionId(),
    // clientRxSessionImpl);
    clientRxSessionImpl.release();
  }

  public void grantAccessOnDeliverFailure(ClientRxSession clientRxSessionImpl, Message request) {
    // TODO Auto-generated method stub
  }

  public void grantAccessOnFailureMessage(ClientRxSession clientRxSessionImpl) {
    // TODO Auto-generated method stub
  }

  public void grantAccessOnTxExpire(ClientRxSession clientRxSessionImpl) {
    // TODO Auto-generated method stub
  }

  public void indicateServiceError(ClientRxSession clientRxSessionImpl) {
    // TODO Auto-generated method stub
  }

  public long[] getApplicationIds() {
    // FIXME: What should we do here?
    return new long[]{16777236};
  }
}
