/** ListModelMapProxy.java.

 Purpose:

 Description:

 History:
 Mon Jun 08 11:30:22 CST 2021, Created by jameschu

 Copyright (C) 2021 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
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
import org.zkoss.lang.Objects;
import org.zkoss.util.Pair;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.PagingListener;
import org.zkoss.zul.ext.SelectionControl;

/**
 * A ListModelMap Proxy
 *
 * @author jameschu
 * @since 9.6.0
 */
public class ListModelMapProxy<K, V> extends ListModelMap<K, V> implements Proxy, FormProxyObject, Serializable {
	private static final long serialVersionUID = 20210608113022L;
	private static final Logger log = LoggerFactory.getLogger(ListModelMapProxy.class);
	private MapForCache<K, V> _cache;
	private ListModelMap<K, V> _origin;
	private boolean _dirty;
	private boolean isImmutableElements;
	private ProxyNode _node;

	public ListModelMapProxy(ListModelMap<K, V> origin, Annotation[] callerAnnots) {
		_origin = origin;
		_cache = new ListModelMapProxy<K, V>.MapForCache<>(origin.size());
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

	private ListModelMap<K, V> initCache() {
		ListModelMap<K, V> cache = new ListModelMap<>(getOriginObject().size());
		return cache;
	}

	private ListModelMap<K, V> getCache() {
		return _cache;
	}

	//-- AbstractListModel --//
	public void addListDataListener(ListDataListener l) {
		getCache().addListDataListener(l); //initial setting
	}

	public void removeListDataListener(ListDataListener l) {
		getCache().removeListDataListener(l); //initial setting
	}

	public Set<Entry<K, V>> getSelection() {
		return getCache().getSelection();
	}

	public void setSelection(Set<Entry<K, V>> selection) {
		getCache().setSelection(selection);
		setDirty(true);
	}

	public boolean isSelected(Object obj) {
		return getCache().isSelected(obj);
	}

	public boolean isSelectionEmpty() {
		return getCache().isSelectionEmpty();
	}

	public boolean addToSelection(Entry<K, V> obj) {
		boolean result = getCache().addToSelection(obj);
		if (result)
			setDirty(true);
		return result;
	}

	public boolean removeFromSelection(Object obj) {
		boolean result = getCache().removeFromSelection(obj);
		if (result)
			setDirty(true);
		return result;
	}

	public void clearSelection() {
		if (!getCache().getSelection().isEmpty()) {
			getCache().clearSelection();
			setDirty(true);
		}
	}

	public void setMultiple(boolean multiple) {
		if (getCache().isMultiple() != multiple) {
			getCache().setMultiple(multiple);
			setDirty(true);
		}
	}

	public void setSelectionControl(SelectionControl ctrl) {
		getCache().setSelectionControl(ctrl); //initial setting
	}

	public SelectionControl getSelectionControl() {
		return getCache().getSelectionControl(); //initial setting
	}

	//For Backward Compatibility//
	public void addSelection(Entry<K, V> obj) {
		addToSelection(obj);
	}

	public void removeSelection(Object obj) {
		removeFromSelection(obj);
	}

	public int getPageSize() {
		return getCache().getPageSize();
	}

	public void setPageSize(int size) throws WrongValueException {
		getCache().setPageSize(size);
		setDirty(true);
	}

	public int getPageCount() {
		return getCache().getPageCount();
	}

	public int getActivePage() {
		return getCache().getActivePage();
	}

	public void setActivePage(int pg) throws WrongValueException {
		getCache().setActivePage(pg);
		setDirty(true);
	}

	public void addPagingEventListener(PagingListener l) {
		getCache().addPagingEventListener(l); //initial setting
	}

	public void removePagingEventListener(PagingListener l) {
		getCache().removePagingEventListener(l); //initial setting
	}

	//-- ListModelMap --//
	public Map<K, V> getInnerMap() {
		return getCache().getInnerMap();
	}

	public int getSize() {
		return getCache().getSize();
	}

	public Map.Entry<K, V> getElementAt(int j) {
		return getCache().getElementAt(j);
	}

	public boolean containsKey(Object key) {
		return getCache().containsKey(createProxyObject(key));
	}

	public boolean containsValue(Object value) {
		Iterator<V> it = getCache().values().iterator();
		Object proxyValue = createProxyObject(value);
		while (it.hasNext()) {
			if (AbstractCollectionProxy.testEquals(it.next(), proxyValue))
				return true;
		}
		return false;
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return getCache().entrySet();
	}

	public boolean equals(Object o) {
		return getCache().equals(o);
	}

	public String toString() {
		return getCache().toString();
	}

	public int hashCode() {
		return getCache().hashCode();
	}

	public boolean isEmpty() {
		return getCache().isEmpty();
	}

	public V get(Object key) {
		return getCache().get(createProxyObject(key));
	}

	public V put(K key, V value) {
		V o = createProxyObject(value);
		if (o instanceof FormProxyObject) {
			setCreatedProxyPath((FormProxyObject) o, key);
		}
		setDirty(true);
		return getCache().put(createProxyObject(key), o);
	}

	public V remove(Object key) {
		setDirty(true);
		return getCache().remove(createProxyObject(key));
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}

	public void clear() {
		setDirty(true);
		getCache().clear();
	}

	public Set<K> keySet() {
		return getCache().keySet();
	}

	public Collection<V> values() {
		return getCache().values();
	}

	public int indexOfKey(Object o) {
		return getCache().indexOfKey(createProxyObject(o));
	}

	public int indexOf(Object o) {
		return getCache().indexOf(createProxyObject(o));
	}

	public int size() {
		return getCache().size();
	}

	//-- Sortable --//
	@Override
	public void sort(Comparator<Map.Entry<K, V>> cmpr, final boolean ascending) {
		getCache().sort(cmpr, ascending);
		setDirty(true);
	}

	public void sort() {
		getCache().sort();
		setDirty(true);
	}

	public String getSortDirection(Comparator<Map.Entry<K, V>> cmpr) {
		return getCache().getSortDirection(cmpr);
	}

	public Object clone() {
		return getCache().clone();
	}

	public ListModelMap<K, V> getOriginObject() {
		return _origin;
	}

	public void onDirtyChange() {
		ProxyHelper.callOnDirtyChange(_node);
	}

	public void onDataChange(Object o) {
		ProxyHelper.callOnDataChange(_node, new Object[]{o, "."});
	}

	protected void setDirty(boolean d) {
		if (_dirty != d) {
			_dirty = d;
			onDirtyChange();
		}
		if (d)
			onDataChange(this);
	}

	//-- Proxy --//
	public void setHandler(MethodHandler mi) {
		throw new UnsupportedOperationException("Not support!");
	}

	public boolean isFormDirty() {
		if (_dirty) {
			return true;
		} else {
			for (Map.Entry<K, V> me : getCache().entrySet()) {
				if (me.getValue() instanceof FormProxyObject) {
					if (((FormProxyObject) me.getValue()).isFormDirty())
						return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void resetFromOrigin() {
		getCache().clear();
		setDirty(false);
		for (Map.Entry<K, V> me : ((Map<K, V>) getOriginObject()).entrySet()) {
			V o = createProxyObject(me.getValue());
			getCache().put(createProxyObject(me.getKey()), o);
			if (o instanceof FormProxyObject)
				setCreatedProxyPath((FormProxyObject) o, me.getKey());
		}
	}

	@SuppressWarnings("unchecked")
	public void submitToOrigin(BindContext ctx) {
		_origin.clear();
		for (Map.Entry<K, V> me : getCache().entrySet()) {
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

	private <T extends Object> T createProxyObject(T t) {
		return isImmutableElements ? t : ProxyHelper.createProxyIfAny(t);
	}

	//ZK-3185: Enable form validation with reference and collection binding
	private void setCreatedProxyPath(FormProxyObject fpo, Object key) {
		fpo.setPath("['" + key + "']", _node);
	}

	public void cacheSavePropertyBinding(String property, SavePropertyBinding s) {
		ProxyHelper.cacheSavePropertyBinding(_node, _node.getProperty() + "['" + property + "']", s);
	}

	public Set<Pair<String, SavePropertyBinding>> collectCachedSavePropertyBinding() {
		throw new UnsupportedOperationException("Not support!");
	}

	public void setPath(String property, ProxyNode parent) {
		if (property == null && _node != null) { // means update
			_node.setParent(parent);
		} else {
			_node = new ProxyNodeImpl(property, parent);
			for (Entry<K, V> e : getCache().entrySet()) {
				if (e.getValue() instanceof FormProxyObject)
					((FormProxyObject) getCache().get(e.getKey())).setPath(null, _node);
			}
		}
	}

	private class MapForCache<K, V> extends ListModelMap<K, V> {
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
			factory.setUseWriteReplace(false);
			factory.setSuperclass(AbstractSet.class);
			factory.createClass();
			try {
				if (isEntry) {
					proxy = _entrySetProxy = (Set<Map.Entry<K, V>>) factory.createClass().newInstance();
					((Proxy) _entrySetProxy).setHandler(new ListModelMapProxy.SetHandlerForCache(super.entrySet()));
				} else {
					proxy = _keySetProxy = (Set<K>) factory.createClass().newInstance();
					((Proxy) _keySetProxy).setHandler(new ListModelMapProxy.SetHandlerForCache(super.keySet()));
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
