/* VirtualIdSpace.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 20 12:51:00 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.ext.NonFellow;

/**
 * A virtual IdSpace used when a root component does not belong to any page,
 * nor implements {@link IdSpace}.
 * It is used if the component has any child, while
 * {@link VirtualSingleIdSpace} is used if the root component has no child.
 * @author tomyeh
 * @since 6.0.0
 */
/*package*/ class VirtualIdSpace implements IdSpace {
	private final Component _owner;
	private final Map<String, Component> _fellows = new HashMap<String, Component>(2);

	/*package*/ VirtualIdSpace(Component owner) {
		if (owner instanceof IdSpace || owner.getParent() != null || owner.getPage() != null)
			throw new InternalError("wrong! " + owner + ":" + owner.getParent() + ":" + owner.getPage());
		init(_fellows, _owner = owner);
	}
	private static void init(Map<String, Component> fellows, Component comp) {
		if (!(comp instanceof NonFellow)) {
			final String compId = comp.getId();
			if (!AbstractComponent.isAutoId(compId))
				fellows.put(compId, comp);
		}
		if (!(comp instanceof IdSpace))
			for (Component child = comp.getFirstChild(); child != null;
			child = child.getNextSibling())
				init(fellows, child); //recursive
	}

	//IdSpace//
	@Override
	public Component getFellow(String id) throws ComponentNotFoundException {
		final Component comp = getFellowIfAny(id);
		if (comp == null)
			throw new ComponentNotFoundException(id);
		return comp;
	}
	@Override
	public Component getFellowIfAny(String id) {
		return _fellows.get(id);
	}
	@Override
	public Collection<Component> getFellows() {
		return _fellows.values();
	}
	@Override
	public boolean hasFellow(String id) {
		return _fellows.containsKey(id);
	}

	@Override
	public Component getFellow(String id, boolean recurse)
	throws ComponentNotFoundException {
		return getFellow(id);
	}
	@Override
	public Component getFellowIfAny(String id, boolean recurse) {
		return getFellowIfAny(id);
	}
	@Override
	public boolean hasFellow(String id, boolean recurse) {
		return hasFellow(id);
	}

	//Scope//
	@Override
	public Map<String, Object> getAttributes() {
		return _owner.getAttributes();
	}
	@Override
	public Object getAttribute(String name) {
		return _owner.getAttribute(name);
	}
	@Override
	public boolean hasAttribute(String name) {
		return _owner.hasAttribute(name);
	}
	@Override
	public Object setAttribute(String name, Object value) {
		return _owner.setAttribute(name, value);
	}
	@Override
	public Object removeAttribute(String name) {
		return _owner.removeAttribute(name);
	}

	@Override
	public Object getAttribute(String name, boolean recurse) {
		return _owner.getAttribute(name, recurse);
	}
	@Override
	public boolean hasAttribute(String name, boolean recurse) {
		return _owner.hasAttribute(name, recurse);
	}
	@Override
	public Object setAttribute(String name, Object value, boolean recurse) {
		return _owner.setAttribute(name, value, recurse);
	}
	@Override
	public Object removeAttribute(String name, boolean recurse) {
		return _owner.removeAttribute(name, recurse);
	}

	@Override
	public boolean addScopeListener(ScopeListener listener) {
		return _owner.addScopeListener(listener);
	}
	@Override
	public boolean removeScopeListener(ScopeListener listener) {
		return _owner.removeScopeListener(listener);
	}
}
