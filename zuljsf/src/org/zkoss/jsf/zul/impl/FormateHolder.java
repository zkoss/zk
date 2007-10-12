/* FormateHolder.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 16, 2007 12:43:49 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.impl;

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;

import org.zkoss.lang.Objects;

/**
 * A base class implementation of StateHolder to keep a format.
 * @author Dennis.Chen
 *
 */
/*package*/ class FormateHolder implements StateHolder{

	protected String _format ;
	
	FormateHolder(){
		
	}
	
	FormateHolder(String format){
		_format = format;
	}

	protected void formatChanged(){
		
	}
	
	public void setFormat(String format) {
		
		if(Objects.equals(_format, format)) return;
		this._format = format;
		formatChanged();
	}
	
	public String getFormat(){
		return _format;
	}
	/* always return false in this method.
	 * @see javax.faces.component.StateHolder#isTransient()
	 */
	public boolean isTransient() {
		return false;//always non-transient.
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		setFormat((String)values[0]);
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
	 */
	public Object saveState(FacesContext context) {
		Object values[] = new Object[1];
		values[0] = _format;
		return values;
	}

	/* do nothing in this method.
	 * @see javax.faces.component.StateHolder#setTransient(boolean)
	 */
	public void setTransient(boolean newTransientValue) {
		//always non-transient.
	}
	
}
