/* ConverterBean.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/8/22 3:01:00 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsfdemo;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * @author Dennis.Chen
 *
 */
public class ConverterBean {

	Date value = new Date();

	public Date getValue() {
		return value;
	}

	public void setValue(Date value) {
		this.value = value;
	}

	public String doSubmit(){
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Process value after conversion:"+value));
		return null;
	}
	
	
}
