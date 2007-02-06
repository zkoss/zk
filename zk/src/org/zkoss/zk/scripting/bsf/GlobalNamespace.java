/* GlobalNamespace.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Feb  6 15:32:00     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.bsf;

import org.apache.bsf.BSFException;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Method;

/**
 * Represent the global namespace of the interpreter. It is the one
 * returned by {@link org.zkoss.zk.scripting.Interpreter#getNamespace}.
 *
 * @author tomyeh
 */
public class GlobalNamespace implements Namespace {
	private Namespace _parent;
	private final BSFManager _manager;

	public GlobalNamespace(BSFManager manager) {
		_manager = manager;
	}

	//Namespace//
	public Class getClass(String clsnm) {
		return null;
	}
	public Object getVariable(String name, boolean local) {
		return _manager.getDeclaredBean(name);
	}
	public void setVariable(String name, Object value, boolean local) {
		try {
			if (value != null)
				_manager.declareBean(name, value, value.getClass());
			else
				_manager.declareBean(name, null, null);
		} catch (BSFException ex) {
			throw new UiException(ex);
		}
	}
	public void unsetVariable(String name) {
		try {
			_manager.undeclareBean(name);
		} catch (BSFException ex) {
			throw new UiException(ex);
		}
	}

	public Method getMethod(String name, Class[] argTypes, boolean local) {
		return null;
	}

	public Namespace getParent() {
		return _parent;
	}
	public void setParent(Namespace parent) {
		_parent = parent;
	}

	public Object getNativeNamespace() {
		return null;
	}

	public Object clone(Component owner, String id) {
		return new GlobalNamespace(_manager);
	}

	public void write(java.io.ObjectOutputStream s, Filter filter)
	throws java.io.IOException {
		//TODO
	}
	public void read(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		//TODO
	}
}
