/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat, Inc. and individual contributors
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

package org.jdiameter.api.cxdx;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.cxdx.events.JLocationInfoRequest;
import org.jdiameter.api.cxdx.events.JMultimediaAuthRequest;
import org.jdiameter.api.cxdx.events.JPushProfileAnswer;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer;
import org.jdiameter.api.cxdx.events.JServerAssignmentRequest;
import org.jdiameter.api.cxdx.events.JUserAuthorizationRequest;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface ClientCxDxSession extends AppSession, StateMachine {

  void sendUserAuthorizationRequest(JUserAuthorizationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendServerAssignmentRequest(JServerAssignmentRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendLocationInformationRequest(JLocationInfoRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendMultimediaAuthRequest(JMultimediaAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendRegistrationTerminationAnswer(JRegistrationTerminationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  void sendPushProfileAnswer(JPushProfileAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
