/* SimpleNamespace.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb  2 18:00:26     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

import java.util.Map;
import java.util.HashMap;

/**
 * A simple namespace which is independent of the implementation of
 * {@link Interpreter}.
 *
 * <p>Currently, it is used by {@link org.zkoss.zk.ui.AbstractComponent}
 * to 'cache' variables before the component is attached to a page.
 * Once the component is attached to a page, the interpreter-dependent
 * namespace will be created and whatever defined here is copied.
 *
 * @author tomyeh
 */
public class SimpleNamespace extends AbstractNamespace {
	private final String _id;
	private Namespace _parent;
	private final Map _vars = new HashMap();

	public SimpleNamespace(String id) {
		_id = id;
	}
	/** Returns the identifier. */
	public String getId() {
		return _id;
	}

	//Namespace//
	public Class getClass(String clsnm) throws ClassNotFoundException {
		throw new ClassNotFoundException(clsnm);
	}
	public Object getVariable(String name, boolean local) {
		return _vars.get(name);
	}
	public void setVariable(String name, Object value, boolean local) {
		_vars.put(name, value);
	}
	public void unsetVariable(String name) {
		_vars.remove(name);
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
		throw new UnsupportedOperationException();
	}

	public void copy(Namespace from, Filter filter) {
	}
	public void write(java.io.ObjectOutputStream s, Filter filter)
	throws java.io.IOException {
	}
	public void read(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
	}
}
