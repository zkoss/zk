/* TreeModelConverter.java

	Purpose:
		
	Description:
		
	History:
		2012/1/13 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter.sys;

import java.io.Serializable;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.TreeModel;

/**
 * @author dennis
 *
 */
public class TreeModelConverter implements Converter,Serializable{
	private static final long serialVersionUID = 1L;

	@Override
	public Object coerceToUi(Object val, Component component, BindContext ctx) {
		if(val instanceof TreeModel){
			component.setAttribute(BinderImpl.MODEL, val); //ZK-758. @see AbstractRenderer#addItemReference
		}
		return val;
	}

	@Override
	public Object coerceToBean(Object val, Component component, BindContext ctx) {
		return val;
	}

}
