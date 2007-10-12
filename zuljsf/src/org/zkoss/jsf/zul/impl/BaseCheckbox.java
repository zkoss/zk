/* BaseCheckbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 8, 2007 5:48:27 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.impl;

import java.util.Map;

import javax.faces.context.FacesContext;



/**
 * The Base implementation of Checkbox. 
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * @author Dennis.Chen
 *
 */
abstract public class BaseCheckbox extends BranchInput {


	/**
	 * Override Method, Return ZUL Component attribute name which can handler the submitting of input. 
	 * Always return "checked"
	 * @see ClientInputSupport
	 */
	public String getMappedAttributeName(){
		return "checked";
	}
	
	/**
	 * Override Method, Decode value in request's parameter. 
	 * call by {@link #decode(FacesContext)}
	 */
	protected void clientInputDecode(FacesContext context) {
		String clientId = this.getClientId(context);
		Map requestMap = context.getExternalContext().getRequestParameterMap();
		if (requestMap.containsKey(clientId)) {
			String newValue = (String)context.getExternalContext().getRequestParameterMap().get(clientId);
			if("on".equals(newValue)){
				setSubmittedValue("true");
				return;
			}
		}
		setSubmittedValue("false");
	}

}
