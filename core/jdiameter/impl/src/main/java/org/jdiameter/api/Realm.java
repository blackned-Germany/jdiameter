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

/**
 * Base Realm interface.
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface Realm {

  /**
   * Return name of this realm
   * 
   * @return name
   */
  public String getName();

  /**
   * Return applicationId associated with this realm
   * 
   * @return applicationId
   */
  public ApplicationId getApplicationId();

  /**
   * Return realm local action for this realm
   * 
   * @return realm local action
   */
  public LocalAction getLocalAction();

  /**
   * Return true if this realm is dynamic updated
   * 
   * @return true if this realm is dynamic updated
   */
  public boolean isDynamic();

  /**
   * Return expiration time for this realm in milisec
   * 
   * @return expiration time
   */
  public long getExpirationTime();

  /**
   * Returns true if realm is local. Local means that it is defined as local(not action) realm for this peer.
   * @return
   */
  public boolean isLocal();

}
