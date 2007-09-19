/* MessageFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 9, 2007 2:16:26 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.impl;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * For handling default JSF Messages.
 * 
 * @author Dennis.Chen
 *
 */
public class MessageFactory {

	public static final String CONVERSION_MESSAGE_ID = "zul.jsf.conversion.message";
	public static final String REQUIRED_MESSAGE_ID = "zul.jsf.required.message";

	
	/**
	 * Get default message by message id
	 * @param context a FacesContext instance
	 * @param messageid the message id.
	 * @return a FacesMessage instance
	 */
	public static FacesMessage getMessage(FacesContext context, String messageid) {
		String message;
		if(CONVERSION_MESSAGE_ID.equals(messageid)){
			message = "Formate Error, Cann't convert.";
		}else if (REQUIRED_MESSAGE_ID.equals(messageid)){
			message = "Value Required";
		}else{
			message = messageid;
		}
		return new FacesMessage(message);
	}

}
