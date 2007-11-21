/* BaseInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 7, 2007 5:56:04 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.jsf.zul.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;

import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.util.Initiator;

/**
 * The skeletal class used to implement the Initiator 
 * 
 * @author Dennis.Chen
 * 
 */
abstract public class BaseInit extends AbstractComponent {
	private Map _compAttrMap;

	private String _use;

	/**
	 * Override Method, write a special mark denoting the component in this
	 * method
	 */
	public void encodeEnd(FacesContext context) throws IOException {
		if (!isRendered() || !isEffective())
			return; //nothing to do
		Map requestMap = context.getExternalContext().getRequestMap();

		Initiators inits = (Initiators) requestMap.get(Initiators.class.getName());
		if (inits == null)
			requestMap.put(Initiators.class.getName(), inits = new Initiators());

		Initiator init;
		try {
			init = (Initiator) Classes.forNameByThread(_use).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}

		List args = new ArrayList();
		for (Iterator itor = _compAttrMap.entrySet().iterator(); itor.hasNext();) {
			Map.Entry entry = (Entry) itor.next();
			// process set value,
			String prop = (String) entry.getKey();
			Object value = _compAttrMap.get(prop);

			if (!prop.startsWith("arg"))
				throw new IllegalArgumentException("Declared attribute:" + prop
						+ " is illegal. Please use arg[int] instead.");
			args.add(Integer.parseInt(prop.substring(3)), value);

		}

		inits.addInitiator(init, args);
	}

	
	

	/**
	 * Returns the class name that is used to implement the Initiator
	 * <p>
	 * Default: null
	 * 
	 * @return the class name used to implement the ZUL Component, or null to
	 *         use the default
	 */
	public String getUse() {
		return _use;
	}

	/**
	 * Sets the class that implements {@link  Initiator}.
	 * 
	 * @param use
	 *            a class name with derived class which is implements
	 *            {@link Initiator}
	 * @throws IllegalArgumentException
	 *             if input class can't be found or is not implement Initiator
	 */
	public void setUse(String use) {
		this._use = use;
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
		Object values[] = new Object[4];
		values[0] = super.saveState(context);
		values[1] = _use;
		Object m[] = saveAttachedMapState(context, _compAttrMap);
		values[2] = m[0];
		values[3] = m[1];
		return (values);

	}

	/**
	 * Override Method, restore the state of this component.
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_use = (String) values[1];
		_compAttrMap = (Map) restoreAttachedMapState(context, values[2], values[3]);
	}
}
