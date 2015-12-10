/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
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

package org.jdiameter.api;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * This interface append to base interface specific methods for
 * creating and send raw diameter messages
 * 
 * @version 1.5.1 Final
 *
 * @author erick.svenson@yahoo.com
 * @author artem.litvinov@gmail.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RawSession extends BaseSession, Wrapper {

  /**
   * Returns a new message object with the specified command code, applicationId
   * and predefined avps.
   * @param commandCode code of message
   * @param applicationId applicationId of destination application
   * @param avp array of avps
   * @return new message object
   */
  Message createMessage(int commandCode, ApplicationId applicationId, Avp... avp);

  /**
   * Returns a new message object with the predefined Command-code, ApplicationId, HopByHopIdentifier, EndToEndIdentifier
   * This method allow created message from storage or created specific message.
   * @param commandCode code of message
   * @param applicationId applicationId of destination application
   * @param hopByHopIdentifier hop by hop identifier of message
   * @param endToEndIdentifier end to end identifier of message
   * @param avp array of avps
   * @return new message object
   */
  Message createMessage(int commandCode, ApplicationId applicationId, long hopByHopIdentifier, long endToEndIdentifier, Avp... avp);

  /**
   * Returns a new message object with the copy of parent message header
   * @param message origination message
   * @param copyAvps if true all avps will be copy to new message
   * @return Returns a new message object with the copy of parent message header
   */
  Message createMessage(Message message, boolean copyAvps);

  /**
   * Sends and wait response message with default timeout
   * @param message request/answer diameter message
   * @param listener event listener
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void send(Message message, EventListener<Message, Message> listener) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Sends and wait response message with defined timeout
   * @param message  request/answer diameter message
   * @param listener  event listener
   * @param timeOut value of timeout
   * @param timeUnit type of timeOut value
   * @throws InternalException  The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void send(Message message, EventListener<Message, Message> listener, long timeOut, TimeUnit timeUnit) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Sends and wait response message with default timeout
   * @param message request/answer diameter message
   * @return InFuture result of an asynchronous operation
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  Future<Message> send(Message message) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Sends and wait response message with defined timeout
   * @param message  request/answer diameter message
   * @return InFuture result of an asynchronous operation
   * @param timeOut value of timeout
   * @param timeUnit type of timeOut value
   * @throws InternalException  The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  Future<Message> send(Message message, long timeOut, TimeUnit timeUnit) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}