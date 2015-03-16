/** MapProxy.java.

	Purpose:
		
	Description:
		
	History:
		4:14:18 PM Dec 26, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.sys.FormBinding;

/**
 * A proxy object to implement <tt>Map</tt>
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public class MapProxy<K, V> implements Map<K, V>, Proxy, FormProxyObject,
		Serializable, FormProxyObjectListener{
	private Map<K, V> _cache;
	private boolean _dirty;
	private Map<K, V> _origin;
	//F80: formProxyObject support notifyChange with Form.isDirty
	private FormProxyObjectListener _listener;

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
		setDirty(false);
		for (Map.Entry<K, V> me : ((Map<K, V>)getOriginObject()).entrySet()) {
			_cache.put(me.getKey(), createProxyObject(me.getValue()));
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
		setDirty(false);
	}

	//F80: formProxyObject support notifyChange with Form.isDirty
	public void addFormProxyObjectListener(FormProxyObjectListener l) {
		if (_listener == null) _listener = l;
	}
	
	public void onDirtyChange() {
		if (_listener != null) _listener.onDirtyChange();
	}
	
	public void onDataChange(Object o) {
		if (_listener != null) _listener.onDataChange(o);
	}	
	
	protected void setDirty(boolean d) {
		if (_dirty != d) {
			_dirty = d;
			onDirtyChange();
		}
		if (d) onDataChange(this);
	}
	
	public boolean isFormDirty() {
		if (_dirty) {
			return true;
		} else {
			for (Map.Entry<K, V> me : _cache.entrySet()) {
				if (me.getValue() instanceof FormProxyObject) {
					if (((FormProxyObject) me.getValue()).isFormDirty())
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
		return _cache.containsKey(createProxyObject(key));
	}

	public boolean containsValue(Object value) {
		Iterator<V> it = _cache.values().iterator();
		Object proxyValue = createProxyObject(value);
		while (it.hasNext()) {
			if (AbstractCollectionProxy.testEquals(it.next(), proxyValue))
				return true;
		}
		return false;
	}

	public V get(Object key) {
		return _cache.get(createProxyObject(key));
	}

	public V put(K key, V value) {
		setDirty(true);
		return _cache.put(createProxyObject(key), createProxyObject(value));
	}

	public V remove(Object key) {
		setDirty(true);
		return _cache.remove(createProxyObject(key));
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
	}

	public void clear() {
		setDirty(true);
		_cache.clear();
	}

	public Set<K> keySet() {
		return _cache.keySet();
	}

	public Collection<V> values() {
		return _cache.values();
	}
	
	public void setFormOwner(Object owner, FormBinding binding) {
		throw new IllegalAccessError("Not supported");
	}
	
	//F80: formProxyObject support notifyChange with Form.isDirty
	private <T extends Object> T createProxyObject(T t) {
		T p = ProxyHelper.createProxyIfAny(t);
		if (p instanceof FormProxyObject) {
			FormProxyObject fpo = (FormProxyObject) p;
			fpo.addFormProxyObjectListener(this);
		}
		return p;
	}

}
