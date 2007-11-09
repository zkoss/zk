/* CustomeAttributes.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/08/16  18:10:17 , Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.zkoss.zk.ui.Component;

/**
 * A Implement of customer-attributes tag<br/>
 * @author Dennis.Chen
 *
 */
public class BaseCustomAttributes extends  AbstractComponent {
	private Map _compAttrMap;
	
	public void encodeBegin(FacesContext context) throws IOException{
		super.encodeBegin(context);
		final AbstractComponent ac =
		(AbstractComponent)findAncestorWithClass(this, AbstractComponent.class);
		if (ac instanceof BranchComponent) {
			((BranchComponent)ac).setZULCustomAttribute(_compAttrMap);
		} else {
			throw new IllegalStateException("Must be nested inside the BranchComponent: "+this);
		}
	}
	
	/**
	 * Set dynamic attribute of Initiator
	 * 
	 * @param map
	 * the dynamic attributes.
	 */
	public void setDynamicAttribute(Map map) {
		_compAttrMap = map;
	}

	// ----------------------------------------------------- StateHolder Methods

	/**
	 * Override Method, save the state of this component.
	 */
	public Object saveState(FacesContext context) {
		Object values[] = new Object[3];
		values[0] = super.saveState(context);
		Object m[] = saveAttachedMapState(context, _compAttrMap);
		values[1] = m[0];
		values[2] = m[1];
		return (values);

	}

	/**
	 * Override Method, restore the state of this component.
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_compAttrMap = (Map) restoreAttachedMapState(context, values[1], values[2]);
	}
}
