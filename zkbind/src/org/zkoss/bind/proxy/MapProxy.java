/** MapProxy.java.

	Purpose:
		
	Description:
		
	History:
		4:14:18 PM Dec 26, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.ImmutableElements;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.util.Pair;

/**
 * A proxy object to implement <tt>Map</tt>
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public class MapProxy<K, V> implements Map<K, V>, Proxy, FormProxyObject, Serializable {
	private static final Logger log = LoggerFactory.getLogger(MapProxy.class);
	private Map<K, V> _cache;
	private boolean _dirty;
	private Map<K, V> _origin;
	private List<Annotation> _callerAnnots;
	private static final long serialVersionUID = 20141226161502L;
	private boolean isImmutableElements;
	//ZK-3185: Enable form validation with reference and collection binding
	private ProxyNode _node;

	public MapProxy(Map<K, V> origin, Annotation[] callerAnnots) {
		_origin = origin;
		_cache = new MapForCache<K, V>(origin.size());
		if (callerAnnots != null) {
			for (Annotation annot : callerAnnots) {
				if (annot.annotationType().isAssignableFrom(ImmutableElements.class)) {
					isImmutableElements = true;
					break;
				}
			}
		}
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
		for (Map.Entry<K, V> me : ((Map<K, V>) getOriginObject()).entrySet()) {
			V o = createProxyObject(me.getValue());
			_cache.put(createProxyObject(me.getKey()), o);
			if (o instanceof FormProxyObject)
				setCreatedProxyPath((FormProxyObject) o, me.getKey());
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
	public void onDirtyChange() {
		ProxyHelper.callOnDirtyChange(_node);
	}

	public void onDataChange(Object o) {
		ProxyHelper.callOnDataChange(_node, o);
	}

	protected void setDirty(boolean d) {
		if (_dirty != d) {
			_dirty = d;
			onDirtyChange();
		}
		if (d)
			onDataChange(this);
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
		V o = createProxyObject(value);
		if (o instanceof FormProxyObject)
			setCreatedProxyPath((FormProxyObject) o, key);
		return _cache.put(createProxyObject(key), o);
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

	private <T extends Object> T createProxyObject(T t) {
		return isImmutableElements ? t : ProxyHelper.createProxyIfAny(t);
	}

	//ZK-3185: Enable form validation with reference and collection binding
	private void setCreatedProxyPath(FormProxyObject fpo, Object key) {
		fpo.setPath("['" + key + "']", _node);
	}

	public void cacheSavePropertyBinding(String property, SavePropertyBinding s) {
		ProxyHelper.cacheSavePropertyBinding(_node, _node.getProperty() + "['" + property + "']" , s);
	}

	public Set<Pair<String, SavePropertyBinding>> collectCachedSavePropertyBinding() {
		throw new UnsupportedOperationException("Not support!");
	}
	public void setPath(String property, ProxyNode parent) {
		if (property == null && _node != null) { // means update
			_node.setParent(parent);
		} else {
			_node = new ProxyNodeImpl(property, parent);
			for (Entry<K, V> e : _cache.entrySet()) {
				if (e.getValue() instanceof FormProxyObject)
					((FormProxyObject) _cache.get(e.getKey())).setPath(null, _node);
			}
		}
	}

	private class MapForCache<K, V> extends LinkedHashMap {
		private transient Set<Map.Entry<K, V>> _entrySetProxy = null;
		private transient Set<K> _keySetProxy = null;
		public MapForCache() {
			super();
		}
		public MapForCache(int size) {
			super(size);
		}
		@Override
		public Set<Map.Entry<K, V>> entrySet() {
			return _entrySetProxy == null ? createProxy(true) : _entrySetProxy;
		}
		@Override
		public Set<K> keySet() {
			return _keySetProxy == null ? createProxy(false) : _keySetProxy;
		}
		private Set createProxy(boolean isEntry) {
			Set proxy = null;
			ProxyFactory factory = new ProxyFactory();
			factory.setSuperclass(AbstractSet.class);
			factory.createClass();
			try {
				if (isEntry) {
					proxy = _entrySetProxy = (Set<Map.Entry<K, V>>) factory.createClass().newInstance();
					((Proxy) _entrySetProxy).setHandler(new SetHandlerForCache(super.entrySet()));
				} else {
					proxy = _keySetProxy = (Set<K>) factory.createClass().newInstance();
					((Proxy) _keySetProxy).setHandler(new SetHandlerForCache(super.keySet()));
				}
			} catch (Exception e) {
				//should not error
				log.warn("", e);
			}
			return proxy;
		}
	}

	private class SetHandlerForCache implements MethodHandler {
		private Set _origin;
		public SetHandlerForCache(Set origin) {
			_origin = origin;
		}
		public Object invoke(Object self, Method method, Method proceed, Object[] args) throws Throwable {
			final String mname = method.getName();
			if (mname.equals("contains")) {
				return method.invoke(_origin, createProxyObject(args[0]));
			} else if (mname.equals("remove")) {
				return method.invoke(_origin, createProxyObject(args[0]));
			}
			return method.invoke(_origin, args);
		}
	}
}
