/** MapProxy.java.

	Purpose:
		
	Description:
		
	History:
		4:14:18 PM Dec 26, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;

import org.zkoss.bind.BindContext;

/**
 * A proxy object to implement <tt>Map</tt>
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public class MapProxy<K, V> implements Map<K, V>, Proxy, FormProxyObject,
		Serializable {
	private Map<K, V> _cache;
	private boolean _dirty;
	private Map<K, V> _origin;

	private static final long serialVersionUID = 20141226161502L;

	public MapProxy(Map<K, V> origin) {
		_origin = origin;
		_cache = new LinkedHashMap<K, V>(origin.size());
		resetFromOrigin();
	}

	public Object getOriginObject() {
		return _origin;
	}

	@SuppressWarnings("unchecked")
	protected Object replaceOrigin(Object origin) {
		Object old = _origin;
		_origin = (Map<K, V>) origin;
		return old;
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return _cache.entrySet();
	}

	@SuppressWarnings("unchecked")
	public void resetFromOrigin() {
		_cache.clear();
		_dirty = false;
		for (Map.Entry<K, V> me : ((Map<K, V>)getOriginObject()).entrySet()) {
			_cache.put(me.getKey(), ProxyHelper.createProxyIfAny(me.getValue()));
		}
	}

	@SuppressWarnings("unchecked")
	public void submitToOrigin(BindContext ctx) {
		_origin.clear();
		for (Map.Entry<K, V> me : _cache.entrySet()) {
			V value = me.getValue();
			if (value instanceof FormProxyObject) {
				FormProxyObject proxyValue = (FormProxyObject) value;
				proxyValue.submitToOrigin(ctx);
				_origin.put(me.getKey(), (V) proxyValue.getOriginObject());
			} else {
				_origin.put(me.getKey(), me.getValue());
			}
		}
		_dirty = false;
	}

	public boolean isDirtyForm() {
		if (_dirty) {
			return true;
		} else {
			for (Map.Entry<K, V> me : _cache.entrySet()) {
				if (me.getValue() instanceof FormProxyObject) {
					if (((FormProxyObject) me.getValue()).isDirtyForm())
						return true;
				}
			}
		}
		return false;
	}

	public void setHandler(MethodHandler mi) {
		throw new UnsupportedOperationException("Not support!");
	}

	public int size() {
		return _cache.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean containsKey(Object key) {
		return _cache.containsKey(ProxyHelper.createProxyIfAny(key));
	}

	public boolean containsValue(Object value) {
		Iterator<V> it = _cache.values().iterator();
		Object proxyValue = ProxyHelper.createProxyIfAny(value);
		while (it.hasNext()) {
			if (AbstractCollectionProxy.testEquals(it.next(), proxyValue))
				return true;
		}
		return false;
	}

	public V get(Object key) {
		return _cache.get(ProxyHelper.createProxyIfAny(key));
	}

	public V put(K key, V value) {
		_dirty = true;
		return _cache.put(ProxyHelper.createProxyIfAny(key), ProxyHelper.createProxyIfAny(value));
	}

	public V remove(Object key) {
		_dirty = true;
		return _cache.remove(ProxyHelper.createProxyIfAny(key));
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
	}

	public void clear() {
		_dirty = true;
		_cache.clear();
	}

	public Set<K> keySet() {
		return _cache.keySet();
	}

	public Collection<V> values() {
		return _cache.values();
	}
}
