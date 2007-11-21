/* BaseAttribute.java

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

import java.io.IOException;
import javax.faces.context.FacesContext;

/**
 * The Base implementation of Attribute.
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * @author Dennis.Chen
 */
public class BaseAttribute extends AbstractComponent {


	private String _name = null;
	
	//private RootComponent _rootcomp;
	private BranchComponent _parentcomp;
	
	/**
	 * Override Method, write a body content to parent's attribute
	 * 
	 * @see  BranchComponent#addZULDynamicAttribute(String, Object)
	 */
	public void encodeEnd(FacesContext context) throws IOException {
		if (!isRendered() || !isEffective())
			return; //nothing to do
		if(_name!=null){
			_parentcomp.addZULDynamicAttribute(_name, getBodyContent());
		}
		setBodyContent(null);//clear
	}
	
	/** 
	 * Override method,
	 * We Construct ZUL JSF Component tree here.
	 * This method is called by JSF implementation, deriving class rarely need to invoke this method.
	 */
	public void encodeBegin(FacesContext context) throws IOException{
		if (!isRendered() || !isEffective())
			return; //nothing to do
		super.encodeBegin(context);
		final AbstractComponent ac =
		(AbstractComponent)findAncestorWithClass(this, AbstractComponent.class);
		if (ac instanceof RootComponent) { //root component 
			//do nothing
		} else if (ac instanceof BranchComponent) {
			_parentcomp = (BranchComponent)ac;
		} else {
			throw new IllegalStateException("Must be nested inside the page component: "+this);
		}
	}


	/**
	 * @return name of attribute.
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * set name of attribute.
	 * @param _name
	 */
	public void setName(String _name) {
		this._name = _name;
	}
	
	public Object saveState(FacesContext context) {
		Object values[] = new Object[2];
		values[0] = super.saveState(context);
		values[1] = _name;
		return (values);
	}

	public void restoreState(FacesContext context, Object state) {

		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_name = ((String) values[1]);
	}
	
}
