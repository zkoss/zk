/* LocalNamespace.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Feb  6 15:38:44     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.bsf;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Method;

/**
 * A local namespace returned by {@link Component#getNamespace}.
 * Note: BSF doesn't support the hierachy of namespaces, so the local namespace
 * is actually a proxy of the global namespace ({@link GlobalNamespace}.
 *
 * @author tomyeh
 */
public class LocalNamespace implements Namespace {
	private Namespace _parent;
	private final Component _owner;

	public LocalNamespace(Component owner) {
		if (owner == null)
			throw new IllegalArgumentException("null owner");
		_owner = owner;
	}

	/** Returns the real namespace, or null if not available.
	 * Since BSF doesn't really support the concept of namespace,
	 * we implement LocalNamespace as a proxy to the global one.
	 */
	private Namespace getReal() {
		final Page page = _owner.getPage();
		return page != null ? page.getNamespace(): null;
	}

	//Namespace//
	public Class getClass(String clsnm) {
		final Namespace ns = getReal();
		return ns != null ? ns.getClass(clsnm): null;
	}
	public Object getVariable(String name, boolean local) {
		final Namespace ns = getReal();
		return ns != null ? ns.getVariable(name, local): null;
	}
	public void setVariable(String name, Object value, boolean local) {
		final Namespace ns = getReal();
		if (ns != null) ns.setVariable(name, value, local);
	}
	public void unsetVariable(String name) {
		final Namespace ns = getReal();
		ns.unsetVariable(name);
	}

	public Method getMethod(String name, Class[] argTypes, boolean local) {
		final Namespace ns = getReal();
		return ns != null ? ns.getMethod(name, argTypes, local): null;
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
		return new LocalNamespace(owner);
	}

	public void write(java.io.ObjectOutputStream s, Filter filter)
	throws java.io.IOException {
	}
	public void read(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
	}
}
