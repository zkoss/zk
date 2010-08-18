/* AbstractPage.java

	Purpose:
		
	Description:
		
	History:
		Sun Oct 26 17:42:22     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.zkoss.util.logging.Log;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;

/**
 * A skeletal implementation of {@link Page}.
 *
 * @author tomyeh
 * @since 3.5.2
 */
abstract public class AbstractPage
implements Page, PageCtrl, java.io.Serializable {
	private static final Log log = Log.lookup(AbstractPage.class);

	/** The first root component. */
	private transient AbstractComponent _firstRoot;
	/** The last root component. */
	private transient AbstractComponent _lastRoot;
	/** The number of root components. */
	private int _nRoot;
	/** The readonly root collection. */
	private transient Collection _roots;
	/** A map of fellows. */
	private transient Map _fellows;

	protected AbstractPage() {
		init();
	}
	/** Note: it is private, so not related to PageImpl.init()
	 */
	private void init() {
		_roots = new Roots();
		_fellows = new HashMap();
	}

	//Page//
	public Component getFirstRoot() {
		return _firstRoot;
	}
	public Component getLastRoot() {
		return _lastRoot;
	}

	public boolean hasFellow(String compId) {
		return _fellows.containsKey(compId);
	}
	public Component getFellow(String compId)
	throws ComponentNotFoundException {
		final Component comp = (Component)_fellows.get(compId);
		if (comp == null)
			throw new ComponentNotFoundException("Fellow component not found: "+compId);
		return comp;
	}
	public Component getFellowIfAny(String compId) {
		return (Component)_fellows.get(compId);
	}
	public Collection getFellows() {
		return Collections.unmodifiableCollection(_fellows.values());
	}
	/** The same as {@link #getFellow(String)}.
	 * In other words, the recurse parameter is not applicable.
	 * @since 5.0.0
	 */
	public Component getFellow(String compId, boolean recurse)
	throws ComponentNotFoundException {
		return getFellow(compId);
	}
	/** The same as {@link #getFellowIfAny(String)}.
	 * In other words, the recurse parameter is not applicable.
	 * @since 5.0.0
	 */
	public Component getFellowIfAny(String compId, boolean recurse) {
		return getFellowIfAny(compId);
	}
	/** The same as {@link #hasFellow(String)}.
	 * In other words, the recurse parameter is not applicable.
	 * @since 5.0.0
	 */
	public boolean hasFellow(String compId, boolean recurse) {
		return hasFellow(compId);
	}

	//PageCtrl//
	/*package*/ void addFellow(Component comp) {
		final String compId = comp.getId();
		final Object old = _fellows.put(compId, comp);
		if (old != comp) { //possible due to recursive call
			if (old != null) {
				_fellows.put(((Component)old).getId(), old); //recover
				throw new InternalError("Called shall prevent replicated ID for roots");
			}
		}
	}
	/*package*/ void removeFellow(Component comp) {
		_fellows.remove(comp.getId());
	}

	/*package*/ void addRoot(Component comp) {
		final AbstractComponent nc = (AbstractComponent)comp;
		for (AbstractComponent ac = _firstRoot; ac != null; ac = ac._next) {
			if (ac == nc) {
				log.warning("Ignored adding "+comp+" twice");
				return; //found and ignore
			}
		}

		//Note: addRoot is called by AbstractComponent
		//and it doesn't need to handle comp's _page

		if (_lastRoot == null) {
			_firstRoot = _lastRoot = nc;
			nc._next = nc._prev = null;
		} else {
			_lastRoot._next = nc;
			nc._prev = _lastRoot;
			nc._next = null;
			_lastRoot = nc;
		}
		++_nRoot;
	}
	/*package*/ void removeRoot(Component comp) {
		//Note: when AbstractComponent.setPage0 is called, parent is already
		//null. Thus, we have to check if it is a root component
		if (isMyRoot(comp)) {
			final AbstractComponent oc = (AbstractComponent)comp;
			setNext(oc._prev, oc._next);
			setPrev(oc._next, oc._prev);
			oc._next = oc._prev = null;
			--_nRoot;
		}
	}
	/** Called when a root compent's {@link AbstractComponent#replaceWith}
	 * is called.
	 */
	/*package*/ void onReplaced(AbstractComponent from, AbstractComponent to) {
		if (_firstRoot == from)
			_firstRoot = to;
		if (_lastRoot == from)
			_lastRoot = to;
	}
	private boolean isMyRoot(Component comp) {
		for (AbstractComponent ac = _firstRoot;; ac = ac._next) {
			if (ac == null)
				return false; //ignore (not a root)
			if (ac == comp)
				return true; //found
		}
	}
	private final
	void setNext(AbstractComponent comp, AbstractComponent next) {
		if (comp != null) comp._next = next;
		else _firstRoot = next;
	}
	private final
	void setPrev(AbstractComponent comp, AbstractComponent prev) {
		if (comp != null) comp._prev = prev;
		else _lastRoot = prev;
	}
	/*package*/ void moveRoot(Component comp, Component refRoot) {
		final AbstractComponent nc = (AbstractComponent)comp;
		if (!isMyRoot(comp) || nc._next == refRoot/*nothing changed*/)
			return; 

		//detach nc
		setNext(nc._prev, nc._next);
		setPrev(nc._next, nc._prev);
		nc._next = nc._prev = null;
		--_nRoot;

		//add
		if (refRoot != null) {
			final AbstractComponent ref = (AbstractComponent)refRoot;
			setNext(nc, ref);
			setPrev(nc, ref._prev);
			setNext(ref._prev, nc);
			setPrev(ref, nc);
			++_nRoot;
		} else {
			addRoot(nc); //add to end
		}
	}

	public Collection getRoots() {
		return _roots;
	}
	public void removeComponents() {
		for (AbstractComponent c = _lastRoot; c != null;) {
			AbstractComponent p = c._prev;
			c.detach();
			c = p;
		}
	}

	public void destroy() {
		_firstRoot = null; _nRoot = 0;
		_fellows = new HashMap(1); //not clear() since # of fellows might huge
	}

	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		//write children
		for (AbstractComponent p = _firstRoot; p != null; p = p._next)
			s.writeObject(p);
		s.writeObject(null);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();

		//read children
		for (AbstractComponent q = null;;) {
			final AbstractComponent child = (AbstractComponent)s.readObject();
			if (child == null) {
				_lastRoot = q;
				break; //no more
			}
			if (q != null) q._next = child;
			else _firstRoot = child;
			child._prev = q;
			child._page = this;
			q = child;
		}

		fixFellows(getRoots());
	}
	private final void fixFellows(Collection c) {
		for (Iterator it = c.iterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			final String compId = comp.getId();
			if (compId.length() > 0)
				addFellow(comp);
			if (!(comp instanceof IdSpace))
				fixFellows(comp.getChildren()); //recursive
		}
	}

	//help classes//
	private class Roots extends AbstractCollection {
		public int size() {return _nRoot;}
		public Iterator iterator() {
			return new RootIter(_firstRoot);
		}
	}
	private static class RootIter implements Iterator {
		private AbstractComponent _p;
		private RootIter(AbstractComponent first) {
			_p = first;
		}
		public boolean hasNext() {
			return _p != null;
		}
		public Object next() {
			Object c = _p;
			_p = _p._next;
			return c;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
