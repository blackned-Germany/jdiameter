/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.common.impl.app.ro;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.ro.events.RoCreditControlAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.slf4j.Logger;

/**
 * ...
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public class RoCreditControlAnswerImpl extends AppAnswerEventImpl implements RoCreditControlAnswer {

  private static final long serialVersionUID = 1L;

  protected Logger logger = org.slf4j.LoggerFactory.getLogger(RoCreditControlAnswerImpl.class);

  private static final int CREDIT_CONTROL_FAILURE_HANDLING_AVP_CODE = 427; 
  private static final int DIRECT_DEBITING_FAILURE_HANDLING_AVP_CODE = 428;
  private static final int REQUESTED_ACTION_AVP_CODE = 436;
  private static final int CC_REQUEST_TYPE_AVP_CODE = 416;
  private static final int VALIDITY_TIME_AVP_CODE = 448;

	/**
	 * @param answer
	 */
	public RoCreditControlAnswerImpl(Answer answer) {
		super(answer);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param request
	 * @param vendorId
	 * @param resultCode
	 */
	public RoCreditControlAnswerImpl(Request request, long vendorId, long resultCode) {
		super(request, vendorId, resultCode);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param request
	 * @param resultCode
	 */
	public RoCreditControlAnswerImpl(Request request, long resultCode) {
		super(request, resultCode);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param request
	 */
	public RoCreditControlAnswerImpl(Request request) {
		super(request);
		// TODO Auto-generated constructor stub
	}

public boolean isCreditControlFailureHandlingAVPPresent() {
    return super.message.getAvps().getAvp(CREDIT_CONTROL_FAILURE_HANDLING_AVP_CODE) != null;
  }

  public int getCredidControlFailureHandlingAVPValue() {
    Avp credidControlFailureHandlingAvp = super.message.getAvps().getAvp(CREDIT_CONTROL_FAILURE_HANDLING_AVP_CODE);
    if(credidControlFailureHandlingAvp != null) {
      try {
        return credidControlFailureHandlingAvp.getInteger32();
      }
      catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Credit-Control-Failure-Handling AVP value", e);
      }
    }

    return -1;
  }

  public boolean isDirectDebitingFailureHandlingAVPPresent() {
    return super.message.getAvps().getAvp(DIRECT_DEBITING_FAILURE_HANDLING_AVP_CODE) != null;
  }

  public int getDirectDebitingFailureHandlingAVPValue() {
    Avp directDebitingFailureHandlingAvp = super.message.getAvps().getAvp(DIRECT_DEBITING_FAILURE_HANDLING_AVP_CODE);
    if(directDebitingFailureHandlingAvp != null) {
      try {
        return directDebitingFailureHandlingAvp.getInteger32();
      }
      catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Direct-Debiting-Failure-Handling AVP value", e);
      }
    }

    return -1;
  }

  public Avp getValidityTimeAvp() {
    return super.message.getAvps().getAvp(VALIDITY_TIME_AVP_CODE);
  }

  public boolean isRequestTypeAVPPresent() {
    return super.message.getAvps().getAvp(CC_REQUEST_TYPE_AVP_CODE) != null;
  }

  public int getRequestTypeAVPValue() {
    Avp requestTypeAvp = super.message.getAvps().getAvp(CC_REQUEST_TYPE_AVP_CODE);
    if(requestTypeAvp != null) {
      try {
        return requestTypeAvp.getInteger32();
      }
      catch (AvpDataException e) {
        logger.debug("Failure trying to obtain CC-Request-Type AVP value", e);
      }
    }

    return -1;
  }

  public boolean isRequestedActionAVPPresent() {
    return super.message.getAvps().getAvp(REQUESTED_ACTION_AVP_CODE) != null;
  }

  public int getRequestedActionAVPValue() {
    Avp requestedActionAvp = super.message.getAvps().getAvp(REQUESTED_ACTION_AVP_CODE);
    if(requestedActionAvp != null) {
      try {
        return requestedActionAvp.getInteger32();
      }
      catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Requested-Action AVP value", e);
      }
    }

    return -1;
  }

}
