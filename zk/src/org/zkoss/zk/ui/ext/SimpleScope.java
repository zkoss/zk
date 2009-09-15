/* SimpleScope.java

	Purpose:
		
	Description:
		
	History:
		Sat Sep 12 13:22:02     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.ext;

import java.util.Iterator;
import java.util.AbstractSet;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentCloneListener;

/**
 * A simple implementation of {@link Scope}.
 * It supports {@link ScopeListener}, but it doesn't support
 * the concept of parent scope.
 * Thus, the deriving class can override
 * {@link #getAttribute(String,Object,boolean)},
 * {@link #hasAttribute(string,Object)},
 * and invoke {@link #notifyParentChange} if the parent is changed.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class SimpleScope implements Scope {
	private final Scope _owner;
	private Map _attrs;
	private List _listeners;

	/** Constructor.
	 * @param owner the real scope that an user can access.
	 * If this object is the scope that an user accesses directly, pass <code>null</code>
	 */
	public SimpleScope(Scope owner) {
		_owner = owner != null ? owner: this;
	}

	//Scope//
	public Map getAttributes() {
		if (_attrs == null) _attrs = new Attrs();
		return _attrs;
	}
	public Object getAttribute(String name) {
		return _attrs != null ? _attrs.get(name): null;
	}
	public boolean hasAttribute(String name) {
		return _attrs != null && _attrs.containsKey(name);
	}
	public Object setAttribute(String name, Object value) {
		if (_attrs == null) _attrs = new Attrs();
		return _attrs.put(name, value);
	}
	public Object removeAttribute(String name) {
		return _attrs != null ? _attrs.remove(name): null;
	}

	/** The same as getAttribute(name). */
	public Object getAttribute(String name, boolean local) {
		return getAttribute(name);
	}
	/** The same as hasAttribute(name). */
	public boolean hasAttribute(String name, boolean local) {
		return hasAttribute(name);
	}

	public boolean addScopeListener(ScopeListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("null");

		if (_listeners == null)
			_listeners = new LinkedList();
		else if (_listeners.contains(listener))
			return false;

		_listeners.add(listener);
		return true;
	}
	public boolean removeScopeListener(ScopeListener listener) {
		return _listeners != null && _listeners.remove(listener);
	}

	/** Invokes {@link ScopeListener#willAdd} for registered
	 * listeners.
	 */
	private void notifyWillAdd(String name, Object value) {
		if (_listeners != null)
			for (Iterator it = _listeners.iterator(); it.hasNext();)
				((ScopeListener)it.next()).willAdd(_owner, name, value);
	}
	/** Invokes {@link ScopeListener#willRemove} for registered
	 * listeners.
	 */
	private void notifyWillRemove(String name) {
		if (_listeners != null)
			for (Iterator it = _listeners.iterator(); it.hasNext();)
				((ScopeListener)it.next()).willRemove(_owner, name);
	}
	/** Invokes {@link ScopeListener#onParentChanged} for registered
	 * listeners.
	 *
	 * @see #addChangeListener
	 */
	public void notifyParentChange(Scope newparent) {
		if (_listeners != null)
			for (Iterator it = _listeners.iterator(); it.hasNext();)
				((ScopeListener)it.next()).didParentChange(_owner, newparent);
	}
	/** Returns a ist of all scope listners (never null).
	 */
	public List getListeners() {
		if (_listeners == null) _listeners = new LinkedList();
		return _listeners;
	}

	//clone//
	/** Clones this scope.
	 * @param owner the owner of the cloned scope.
	 */
	public SimpleScope clone(Scope owner) {
		final SimpleScope clone = new SimpleScope(owner);
		if (_attrs != null) {
			clone._attrs = new HashMap();
			for (Iterator it = _attrs.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				Object val = me.getValue();
				if (val instanceof ComponentCloneListener
				&& owner instanceof Component) {
					val = ((ComponentCloneListener)val).willClone((Component)owner);
					if (val == null) continue; //don't use it in clone
				}
				clone._attrs.put(me.getKey(), val);
			}
		}
		if (_listeners != null) {
			clone._listeners = new LinkedList();
			for (Iterator it = _listeners.iterator(); it.hasNext();) {
				Object val = it.next();
				if (val instanceof ComponentCloneListener
				&& owner instanceof Component) {
					val = ((ComponentCloneListener)val).willClone((Component)owner);
					if (val == null) continue; //don't use it in clone
				}
				clone._listeners.add(val);
			}
		}
		return clone;
	}

	//Helper Class//
	private class Attrs extends HashMap {
		public Object remove(Object key) {
			if (_listeners != null && super.containsKey(key))
				notifyWillRemove((String)key);
			return super.remove(key);
		}
		public Object put(Object key, Object val) {
			notifyWillAdd((String)key, val);
			return super.put(key, val);
		}
		public Set entrySet() {
			return new AttrSet(super.entrySet(), true);
		}
		public Set keySet() {
			return new AttrSet(super.keySet(), false);
		}
		private class AttrSet extends AbstractSet {
			private final Set _set;
			private final boolean _entry;
			private AttrSet(Set set, boolean entry) {
				_set = set;
				_entry = entry;
			}
			public Iterator iterator() {
				return new AttrIter(_set.iterator());
			}
			public int size() {
				return _set.size();
			}
			public boolean add(Object o) {
				if (_entry) {
					final Map.Entry me = (Map.Entry)o;
					notifyWillAdd((String)me.getKey(), me.getValue());
				} else
					notifyWillAdd((String)o, null);
				return _set.add(o);
			}
			public boolean remove(Object o) {
				notifyWillRemove((String)(_entry ? ((Map.Entry)o).getKey(): o));
				return _set.remove(o);
			}
			public boolean contains(Object o) {
				return _set.contains(o);
			}
			private class AttrIter implements Iterator {
				private final Iterator _it;
				private Object _last;
				private AttrIter(Iterator it) {
					_it = it;
				}
				public boolean hasNext() {
					return _it.hasNext();
				}
				public Object next() {
					return _last = _it.next();
				}
				public void remove() {
					if (_last != null) //caller might make a mistake
						notifyWillRemove((String)(_entry ? ((Map.Entry)_last).getKey(): _last));
					_it.remove();
				}
			}
		}
	}
}
