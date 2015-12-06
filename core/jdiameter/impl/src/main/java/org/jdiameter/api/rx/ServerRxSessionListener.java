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

package org.jdiameter.api.rx;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.AppAnswerEvent;

//import ReAuthAnswer;
//import ReAuthRequest;
import org.jdiameter.api.rx.events.RxReAuthRequest;
import org.jdiameter.api.rx.events.RxReAuthAnswer;

//import AbortSessionAnswer;
//import AbortSessionRequest;
import org.jdiameter.api.rx.events.RxAbortSessionRequest;
import org.jdiameter.api.rx.events.RxAbortSessionAnswer;


//import SessionTermRequest;
import org.jdiameter.api.rx.events.RxSessionTermRequest;

import org.jdiameter.api.rx.events.RxAARequest;

/**
 * This interface defines the possible actions for the different states in the server Rx 
 * Interface state machine.
 * 
 * @author <a href="mailto:richard.good@smilecoms.com"> Richard Good </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface ServerRxSessionListener {

  void doAARequest(ServerRxSession session, RxAARequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doSessionTermRequest(ServerRxSession session, RxSessionTermRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doReAuthAnswer(ServerRxSession session, RxReAuthRequest request, RxReAuthAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doAbortSessionAnswer(ServerRxSession session, RxAbortSessionRequest request, RxAbortSessionAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;
}
