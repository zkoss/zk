/* SelectionTestBean.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 15, 2007 11:45:35 AM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsfdemo.test;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * @author Dennis.Chen
 *
 */
public class ListboxBean {

	Object selection = "2";
	
	Object selections = new Object[]{"1","3"};

	public Object getSelection() {
		return selection;
	}

	public void setSelection(Object selection) {
		this.selection = selection;
	}

	public Object getSelections() {
		return selections;
	}

	public void setSelections(Object selections) {
		this.selections = selections;
	}
	
	public String doSubmit(){
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("selection:"+selection));
		context.addMessage(null, new FacesMessage("selections:"+selections));
		
		return null;
	}
}
