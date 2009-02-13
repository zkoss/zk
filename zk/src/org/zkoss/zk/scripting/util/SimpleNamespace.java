/* SimpleNamespace.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May  8 12:56:25     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.util;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.scripting.Namespace;

/**
 * A simple implementation of {@link Namespace}.
 *
 * @author tomyeh
 */
public class SimpleNamespace extends AbstractNamespace {
	private Namespace _parent;
	private final Map _vars;
	private final Component _owner;

	public SimpleNamespace() {
		_vars = new HashMap(8);
		_owner = null;
	}
	/**
	 * @param owner the owner of this namespace.
	 * If not null, the fellow of the owner is considered as part of this namespace.
	 * In other words, {@link #containsVariable} and {@link #getVariable}
	 * will check {@link Component#getFellow}.
	 * @since 3.0.0
	 */
	public SimpleNamespace(Component owner) {
		_vars = new HashMap(8);
		_owner = owner;
	}

	/** Copies all variables from the specified namespace.
	 * Note: only variables local to the specified namespace is copied.
	 */
	public void copy(Namespace ns) {
		for (Iterator it = ns.getVariableNames().iterator(); it.hasNext();) {
			final String name = (String)it.next();
			final Object value = ns.getVariable(name, true);
			_vars.put(name, value);
			notifyAdd(name, value);
		}
	}

	//Namespace//
	public Component getOwner() {
		return _owner;
	}
	public Page getOwnerPage() {
		return _owner != null ? _owner.getPage(): null;
	}
	public Set getVariableNames() {
		return _vars.keySet();
	}
	public boolean containsVariable(String name, boolean local) {
		return _vars.containsKey(name)
		|| (_owner != null && _owner.getFellowIfAny(name) != null)
		|| (!local && _parent != null && _parent.containsVariable(name, false));
	}
	public Object getVariable(String name, boolean local) {
		Object val = _vars.get(name);
		if (val != null || _vars.containsKey(name))
			return val;

		if (_owner != null) {
			val = _owner.getFellowIfAny(name);
			if (val != null)
				return val;
		}
		return local || _parent == null ? null: _parent.getVariable(name, false);
	}
	public void setVariable(String name, Object value, boolean local) {
		if (!local && _parent != null && !_vars.containsKey(name)) {
			for (Namespace p = _parent;;) {
				if (p.getVariableNames().contains(name)) {
					p.setVariable(name, value, true);
					return; //done;
				}
				if ((p = p.getParent()) == null)
					break;
			}
		}

		_vars.put(name, value);
		notifyAdd(name, value);
	}
	public void unsetVariable(String name, boolean local) {
		if (_vars.remove(name) != null || _vars.containsKey(name)) {
			notifyRemove(name);
		} else if (!local && _parent != null) {
			for (Namespace p = _parent; p != null; p = p.getParent()) {
				if (p.getVariableNames().contains(name)) {
					p.unsetVariable(name, true);
					break;
				}
				if ((p = p.getParent()) == null)
					break;
			}
		}
	}

	public Namespace getParent() {
		return _parent;
	}
	public void setParent(Namespace parent) {
		if (_parent != parent) {
			for (Namespace p = parent; p != null; p = p.getParent())
				if (p == this)
					throw new IllegalArgumentException("Recursive namespace: "+this+" with "+parent);
			_parent = parent;
			notifyParentChanged(parent);
		}
	}
}
