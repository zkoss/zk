/* AbstractNamespace.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Feb  8 09:45:18     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.util;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.NamespaceChangeListener;

/**
 * A skeletal class for implementing {@link Namespace}.
 *
 * @author tomyeh
 */
abstract public class AbstractNamespace implements Namespace {
	private List _listeners;

	/** Invokes {@link NamespaceChangeListener#onAdd} for registered
	 * listeners.
	 *
	 * @see #addChangeListener
	 */
	protected void notifyAdd(String name, Object value) {
		if (_listeners != null)
			for (Iterator it = _listeners.iterator(); it.hasNext();)
				((NamespaceChangeListener)it.next()).onAdd(name, value);
	}
	/** Invokes {@link NamespaceChangeListener#onRemove} for registered
	 * listeners.
	 *
	 * @see #addChangeListener
	 */
	protected void notifyRemove(String name) {
		if (_listeners != null)
			for (Iterator it = _listeners.iterator(); it.hasNext();)
				((NamespaceChangeListener)it.next()).onRemove(name);
	}
	/** Invokes {@link NamespaceChangeListener#onParentChanged} for registered
	 * listeners.
	 *
	 * @see #addChangeListener
	 */
	protected void notifyParentChanged(Namespace newparent) {
		if (_listeners != null)
			for (Iterator it = _listeners.iterator(); it.hasNext();)
				((NamespaceChangeListener)it.next()).onParentChanged(newparent);
	}

	//Namespace//
	public boolean addChangeListener(NamespaceChangeListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("null");

		if (_listeners == null)
			_listeners = new LinkedList();
		else if (_listeners.contains(listener))
			return false;

		_listeners.add(listener);
		return true;
	}
	public boolean removeChangeListener(NamespaceChangeListener listener) {
		return _listeners != null && _listeners.remove(listener);
	}
}
