/* SimpleIdSpace.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 20 12:51:00 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.impl;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;

/**
 * A simple implementation of {@link IdSpace}.
 * It is usually used for implementing the so-called virtual ID space
 * ({@link org.zkoss.zk.ui.sys.ExecutionsCtrl#setVirtualIdSpace}.
 * @author tomyeh
 * @since 5.0.4
 */
public class SimpleIdSpace extends SimpleScope implements IdSpace {
	private final Map<String, Component> _fellows = new HashMap<String, Component>();

	public SimpleIdSpace() {
		super(null);
	}

	/** Adds a fellow.
	 */
	public void addFellow(Component comp) {
		_fellows.put(comp.getId(), comp);
	}
	/** Removes a fellow.
	 */
	public void removeFellow(Component comp) {
		_fellows.remove(comp.getId());
	}

	//@Override
	public Component getFellow(String id) throws ComponentNotFoundException {
		final Component comp = getFellowIfAny(id);
		if (comp == null)
			throw new ComponentNotFoundException(id);
		return comp;
	}
	//@Override
	public Component getFellowIfAny(String id) {
		return _fellows.get(id);
	}
	//@Override
	public Collection<Component> getFellows() {
		return _fellows.values();
	}
	//@Override
	public boolean hasFellow(String id) {
		return _fellows.containsKey(id);
	}

	//@Override
	public Component getFellow(String id, boolean recurse)
	throws ComponentNotFoundException {
		return getFellow(id);
	}
	//@Override
	public Component getFellowIfAny(String id, boolean recurse) {
		return getFellowIfAny(id);
	}
	//@Override
	public boolean hasFellow(String id, boolean recurse) {
		return hasFellow(id);
	}
}
