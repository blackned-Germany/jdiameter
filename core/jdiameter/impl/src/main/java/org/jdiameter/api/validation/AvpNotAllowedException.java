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

package org.jdiameter.api.validation;

/**
 * Class to indicate error in AVP add operation.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class AvpNotAllowedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private int avpCode = -1;
  private long vendorId = -1L;

  /**
   * 	
   */
  public AvpNotAllowedException(int code, long vendor) {
    this.avpCode = code;
    this.vendorId = vendor;
  }

  /**
   * 
   * @param message
   */
  public AvpNotAllowedException(String message, int code, long vendor) {
    super(message);
    this.avpCode = code;
    this.vendorId = vendor;
  }

  /**
   * 
   * @param cause
   */
  public AvpNotAllowedException(Throwable cause, int code, long vendor) {
    super(cause);
    this.avpCode = code;
    this.vendorId = vendor;
  }

  /**
   * 
   * @param message
   * @param cause
   */
  public AvpNotAllowedException(String message, Throwable cause, int code, long vendor) {
    super(message, cause);
    this.avpCode = code;
    this.vendorId = vendor;
  }

  public int getAvpCode() {
    return avpCode;
  }

  public long getVendorId() {
    return vendorId;
  }

  @Override
  public String toString() {
    return "AvpNotAllowedExcption [avpCode=" + avpCode + ", vendorId=" + vendorId + ", toString()=" + super.toString() + "]";
  }

}
