/** AbstractCollectionProxy.java.

	Purpose:
		
	Description:
		
	History:
		3:01:37 PM Dec 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.lang.Objects;

/**
 * This class provides a skeletal implementation of the <tt>Collection</tt>
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public abstract class AbstractCollectionProxy<E> implements Collection<E>,
		Proxy, FormProxyObject, Serializable {
	private Collection<E> _cache;
	private static final long serialVersionUID = 20141225142801L;
	private Object _origin;
	protected boolean _dirty;
	public AbstractCollectionProxy(Collection<E> origin) {
		_origin = origin;
		_cache = initCache();
	}
	
	protected abstract Collection<E> initCache();
	
	@SuppressWarnings("unchecked")
	protected <T extends Collection<E>> T getCache() {
		return (T)_cache;
	}

	public Object getOriginObject() {
		return _origin;
	}

	protected Object replaceOrigin(Object origin) {
		Object old = _origin;
		_origin = origin;
		return old;
	}

	public void setHandler(MethodHandler mi) {
		throw new UnsupportedOperationException("Not support!");
	}
	
	public int size() {
		return _cache.size();
	}

	public boolean isEmpty() {
		return _cache.isEmpty();
	}

	public boolean contains(Object o) {
		Iterator<E> e = iterator();
		if (o == null) {
			while (e.hasNext())
				if (testEquals(e.next(), null))
					return true;
		} else {
			while (e.hasNext())
				if (testEquals(o, e.next()))
					return true;
		}
		return false;
	}

	public void clear() {
		_dirty = true;
		_cache.clear();
	}
	
	public Iterator<E> iterator() {
		return _cache.iterator();
	}

	public Object[] toArray() {
		return _cache.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return _cache.toArray(a);
	}
	
	public boolean add(E e) {
		if (_cache.add(ProxyHelper.createProxyIfAny(e))) {
			_dirty = true;
			return true;
		}
		return false;
	}

	public boolean remove(Object o) {
		if (_cache.remove(ProxyHelper.createProxyIfAny(o))) {
			_dirty = true;
			return true;
		}
		return false;
	}

	public boolean addAll(Collection<? extends E> c) {
		boolean modified = false;
		Iterator<? extends E> e = c.iterator();
		while (e.hasNext()) {
		    if (add(e.next()))
			modified = true;
		}
		return modified;
	}


	public boolean containsAll(Collection<?> c) {
		Iterator<?> e = c.iterator();
		while (e.hasNext())
			if (!contains(e.next()))
				return false;
		return true;
	}

	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		Iterator<?> e = iterator();
		while (e.hasNext()) {
			if (c.contains(e.next())) {
				e.remove();
				modified = true;
			}
		}
		return modified;
	}

	public boolean isFormDirty() {
		if (_dirty)
			return true;
		Iterator<?> e = iterator();
		while (e.hasNext()) {
			Object o = e.next();
			if (o instanceof FormProxyObject) {
				if (((FormProxyObject) o).isFormDirty())
					return true;
			}
		}
		return false;
	}

	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		Iterator<E> e = iterator();
		while (e.hasNext()) {
			if (!c.contains(e.next())) {
				e.remove();
				modified = true;
			}
		}
		return modified;
	}
	
	@SuppressWarnings("unchecked")
	public void resetFromOrigin() {
		getCache().clear();
		_dirty = false;
		for (E e : (Collection<E>)getOriginObject()) {
			getCache().add(ProxyHelper.createProxyIfAny(e));
		}
	}

	@SuppressWarnings("unchecked")
	public void submitToOrigin(BindContext ctx) {
		Collection<E> origin = ((Collection<E>)getOriginObject());
		origin.clear();
		for (E e : getCache()) {
			if (e instanceof FormProxyObject) {
				FormProxyObject proxy = (FormProxyObject) e;
				proxy.submitToOrigin(ctx);
				origin.add((E)proxy.getOriginObject());
			} else {
				origin.add(e);
			}	
		}
	}
	
	protected static boolean testEquals(Object e, Object o) {
		if (e == o) {
			return true;
		}
		if (e instanceof FormProxyObject) {
			Object eo = ((FormProxyObject) e).getOriginObject();
			Object oo = o;
			if (o instanceof FormProxyObject) {
				oo = ((FormProxyObject) o).getOriginObject();
			}
			if (Objects.equals(eo, oo))
				return true;
		} else if (o instanceof FormProxyObject) {
			if (Objects.equals(e, ((FormProxyObject) o).getOriginObject()))
				return true;
		} else {
			if (Objects.equals(e, o))
				return true;
		}
		return false;
	}
	
	public void setFormOwner(Object owner, FormBinding binding) {
		throw new IllegalAccessError("Not supported");
	}
}
