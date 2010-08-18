/* SimpleScope.java

	Purpose:
		
	Description:
		
	History:
		Sat Sep 12 13:22:02     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.impl;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.AbstractSet;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.zkoss.lang.reflect.Fields;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.ext.ScopeListener;

/**
 * A simple implementation of {@link Scope}.
 * It supports {@link ScopeListener}, but it doesn't support
 * the concept of parent scope.
 * Thus, the deriving class can override
 * {@link #getAttribute(String,boolean)},
 * {@link #hasAttribute(String,boolean)},
 * and invoke {@link #notifyParentChanged} if the parent is changed.
 *
 * <p>Not thread safe.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class SimpleScope implements Scope {
	private final Scope _owner;
	private Map _attrs;
	private final ScopeListeners _listeners;

	/** Constructor.
	 * @param owner the real scope that an user can access.
	 * If this object is the scope that an user accesses directly, pass <code>null</code>
	 */
	public SimpleScope(Scope owner) {
		_owner = owner != null ? owner: this;
		_listeners = new ScopeListeners(_owner);
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
	public Object getAttribute(String name, boolean recurse) {
		return getAttribute(name);
	}
	/** The same as hasAttribute(name). */
	public boolean hasAttribute(String name, boolean recurse) {
		return hasAttribute(name);
	}
	/** The same as setAttribute(name, value). */
	public Object setAttribute(String name, Object value, boolean recurse) {
		return setAttribute(name, value);
	}
	/** The same as removeAttribute(name). */
	public Object removeAttribute(String name, boolean recurse) {
		return removeAttribute(name);
	}

	public boolean addScopeListener(ScopeListener listener) {
		return _listeners.addScopeListener(listener);
	}
	public boolean removeScopeListener(ScopeListener listener) {
		return _listeners.removeScopeListener(listener);
	}

	/** Invokes {@link ScopeListener#parentChanged} for registered
	 * listeners.
	 *
	 * @see #addScopeListener
	 */
	public void notifyParentChanged(Scope newparent) {
		_listeners.notifyParentChanged(newparent);
	}
	/** Invokes {@link ScopeListener#idSpaceChanged} for registered
	 * listeners.
	 *
	 * @see #addScopeListener
	 * @since 5.0.1
	 */
	public void notifyIdSpaceChanged(IdSpace newIdSpace) {
		_listeners.notifyIdSpaceChanged(newIdSpace);
	}
	/** Returns a ist of all scope listners (never null).
	 */
	public List getListeners() {
		return _listeners.getListeners();
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
					val = willClone((Component)owner, (ComponentCloneListener)val);
					if (val == null) continue; //don't use it in clone
				}
				clone._attrs.put(me.getKey(), val);
			}
		}

		for (Iterator it = _listeners.getListeners().iterator(); it.hasNext();) {
			Object val = it.next();
			if (val instanceof ComponentCloneListener
			&& owner instanceof Component) {
				val = willClone((Component)owner, ((ComponentCloneListener)val));
				if (val == null) continue; //don't use it in clone
			}
			clone._listeners.addScopeListener((ScopeListener)val);
		}
		return clone;
	}
	private static final Object willClone(Component owner, ComponentCloneListener val) {
		try {
			return val.willClone(owner);
		} catch (AbstractMethodError ex) { //backward compatible prior to 5.0
			try {
				final Method m = val.getClass().getMethod(
					"clone", new Class[] {Component.class});
				Fields.setAccessible(m, true);
				return m.invoke(val, new Object[] {owner});
			} catch (Exception t) {
				throw UiException.Aide.wrap(t);
			}
		}
	}

	//Object//
	public String toString() {
		return _attrs != null ? _attrs.toString(): "{}";
	}

	//Helper Class//
	private class Attrs extends HashMap {
		public Object remove(Object key) {
			final Object o = super.remove(key);
			if (o != null) _listeners.notifyRemoved((String)key);
			return o;
		}
		public Object put(Object key, Object val) {
			final Object o = super.put(key, val);
			if (o != null) _listeners.notifyReplaced((String)key, val);
			else _listeners.notifyAdded((String)key, val);
			return o;
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
				if (_set.add(o)) {
					if (_entry) {
						final Map.Entry me = (Map.Entry)o;
						_listeners.notifyAdded((String)me.getKey(), me.getValue());
					} else
						_listeners.notifyAdded((String)o, null);
					return true;
				} else {
					if (_entry) {
						final Map.Entry me = (Map.Entry)o;
						_listeners.notifyReplaced((String)me.getKey(), me.getValue());
					} else
						_listeners.notifyReplaced((String)o, null);
					return false;
				}
			}
			public boolean remove(Object o) {
				if (_set.remove(o)) {
					_listeners.notifyRemoved((String)(_entry ? ((Map.Entry)o).getKey(): o));
					return true;
				}
				return false;
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
					_it.remove();
					_listeners.notifyRemoved((String)(_entry ? ((Map.Entry)_last).getKey(): _last));
				}
			}
		}
	}
}
