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

package org.jdiameter.api.auth;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.auth.events.SessionTermRequest;

/**
 * This interface defines the possible actions that the different states in the
 * Authentication state machine
 * 
 * @version 1.5.1 Final
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface ServerAuthSessionListener {

  /**
   * Notifies this AuthSessionEventListener that the ServerAuthSesssion has recived AuthRequest message.
   * @param session parent application session (FSM)
   * @param request authentication request object
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doAuthRequestEvent(ServerAuthSession session, AppRequestEvent request)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this AuthSessionEventListener that the ServerAuthSesssion has recived ReAuthAnswer message.
   * @param  session parent application session (FSM)
   * @param  request re-authentication request object
   * @param  answer re-authentication answer object
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doReAuthAnswerEvent(ServerAuthSession session,  ReAuthRequest request, ReAuthAnswer answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this AuthSessionEventListener that the ServerAuthSesssion has recived AbortSessionRequest message.
   * @param session parent application session (FSM)
   * @param answer abort session event event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doAbortSessionAnswerEvent(ServerAuthSession session, AbortSessionAnswer answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this AuthSessionEventListener that the ServerAuthSesssion has recived SessionTerminationRequest message.
   * @param session parent application session (FSM)
   * @param request session termination request event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */    
  void doSessionTerminationRequestEvent(ServerAuthSession session, SessionTermRequest request)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Notifies this AuthSessionEventListener that the ServerAuthSesssion has recived not authentication message.
   * @param session parent application session (FSM)
   * @param request request object
   * @param answer answer object
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
  throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
