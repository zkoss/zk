/** ListModelArrayProxy.java.

	Purpose:

	Description:

	History:
		Mon Jun 08 11:30:22 CST 2021, Created by jameschu

	Copyright (C) 2021 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.ImmutableElements;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.lang.Objects;
import org.zkoss.util.Pair;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.PagingListener;
import org.zkoss.zul.ext.SelectionControl;

/**
 * A ListModelArray Proxy
 *
 * @author jameschu
 * @since 9.6.0
 */
public class ListModelArrayProxy<E> extends ListModelArray<E> implements Proxy, FormProxyObject, Serializable {
	private static final long serialVersionUID = 20210608113022L;
	private ListModelArray<E> _cache;
	private ListModelArray<E> _origin;
	private boolean _dirty;
	private boolean isImmutableElements;
	private ProxyNode _node;

	public ListModelArrayProxy(ListModelArray<E> origin, Annotation[] callerAnnots) {
		super(0); //for super class no empty constructor
		_origin = origin;
		_cache = initCache();
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

	private ListModelArray<E> initCache() {
		ListModelArray<E> cache = new ListModelArray<E>(getOriginObject().getSize());
		return cache;
	}

	private ListModelArray<E> getCache() {
		return _cache;
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

	//-- AbstractListModel --//
	public void addListDataListener(ListDataListener l) {
		getCache().addListDataListener(l); //initial setting
	}

	public void removeListDataListener(ListDataListener l) {
		getCache().removeListDataListener(l); //initial setting
	}

	public Set<E> getSelection() {
		return getCache().getSelection();
	}

	public void setSelection(Collection<? extends E> selection) {
		getCache().setSelection(selection);
		setDirty(true);
	}

	public boolean isSelected(Object obj) {
		return getCache().isSelected(obj);
	}

	public boolean isSelectionEmpty() {
		return getCache().isSelectionEmpty();
	}

	public boolean addToSelection(E obj) {
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

	public void setSelectionControl(SelectionControl ctrl) {
		getCache().setSelectionControl(ctrl); //initial setting
	}

	public SelectionControl getSelectionControl() {
		return getCache().getSelectionControl(); //initial setting
	}

	public void addPagingEventListener(PagingListener l) {
		getCache().addPagingEventListener(l); //initial setting
	}

	public void removePagingEventListener(PagingListener l) {
		getCache().removePagingEventListener(l); //initial setting
	}

	//-- ListModelArray --//
	public E get(int index) {
		return getCache().getElementAt(index);
	}

	public void set(int index, E value) {
		getCache().set(index, value);
	}

	public Object[] getInnerArray() {
		return getCache().getInnerArray();
	}

	public int indexOf(Object elm) {
		return getCache().indexOf(elm);
	}

	public boolean notifyChange(E element) {
		return getCache().notifyChange(element);
	}

	//-- ListModel --//
	public int getSize() {
		return getCache().getSize();
	}

	public E getElementAt(int j) {
		return getCache().getElementAt(j);
	}

	//-- Sortable --//
	public void sort(Comparator<E> cmpr, final boolean ascending) {
		getCache().sort(cmpr, ascending);
		setDirty(true);
	}

	public void sort() {
		getCache().sort();
		setDirty(true);
	}

	public String getSortDirection(Comparator<E> cmpr) {
		return getCache().getSortDirection(cmpr);
	}

	//Object//
	public boolean equals(Object o) {
		return getCache().equals(o);
	}

	public int hashCode() {
		return getCache().hashCode();
	}

	public String toString() {
		return getCache().toString();
	}

	public Object clone() {
		return getCache().clone();
	}

	protected void fireSelectionEvent(E e) {
		java.lang.reflect.Method m = null;
		try {
			m = getCache().getClass().getDeclaredMethod("fireSelectionEvent", e.getClass());
			m.setAccessible(true);
			m.invoke(_cache, e);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException noSuchMethodException) {
			noSuchMethodException.printStackTrace();
		}
	}

	//For Backward Compatibility//
	public void addSelection(E obj) {
		addToSelection(obj);
	}

	public void removeSelection(Object obj) {
		removeFromSelection(obj);
	}

	//-- Proxy --//
	public void setHandler(MethodHandler mi) {
		throw new UnsupportedOperationException("Not support!");
	}

	//-- FormProxyObject --//
	public ListModelArray<E> getOriginObject() {
		return _origin;
	}

	public void resetFromOrigin() {
		setDirty(false);
		ListModelArray<E> originObject = getOriginObject();
		int size = getSize();
		ListModelArray<E> cache = getCache();
		for (int i = 0; i < size; i++)
			cache.set(i, originObject.get(i));
	}

	public void submitToOrigin(BindContext ctx) {
		ListModelArray<E> origin = getOriginObject();
		int size = getSize();
		ListModelArray<E> cache = getCache();
		for (int i = 0; i < size; i++) {
			E e = cache.get(i);
			if (e instanceof FormProxyObject) {
				FormProxyObject proxy = (FormProxyObject) e;
				proxy.submitToOrigin(ctx);
				origin.set(i, (E) proxy.getOriginObject());
			} else {
				origin.set(i, e);
			}
		}
		setDirty(false);
	}

	public boolean isFormDirty() {
		if (_dirty)
			return true;
		int size = getSize();
		ListModelArray<E> cache = getCache();
		for (int i = 0; i < size; i++) {
			E e = cache.get(i);
			if (e instanceof FormProxyObject) {
				if (((FormProxyObject) e).isFormDirty())
					return true;
			}
		}
		return false;
	}

	public void setFormOwner(Object owner, FormBinding binding) {
		throw new IllegalAccessError("Not supported");
	}

	public void setPath(String property, ProxyNode parent) {
		if (property == null && _node != null) { // means update
			_node.setParent(parent);
		} else {
			_node = new ProxyNodeImpl(property, parent);
			int size = getSize();
			ListModelArray<E> cache = getCache();
			for (int i = 0; i < size; i++) {
				E e = cache.get(i);
				if (e instanceof FormProxyObject)
					((FormProxyObject) e).setPath("[$INDEX$]", _node);
			}
		}
	}

	public void cacheSavePropertyBinding(String property, SavePropertyBinding s) {
		ProxyHelper.cacheSavePropertyBinding(_node, _node.getProperty() + property, s);
	}

	public Set<Pair<String, SavePropertyBinding>> collectCachedSavePropertyBinding() {
		throw new UnsupportedOperationException("Not support!");
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

	protected <T extends Object> T createProxyObject(T t) {
		T p = isImmutableElements ? t : ProxyHelper.createProxyIfAny(t);
		if (p instanceof FormProxyObject)
			((FormProxyObject) p).setPath("[$INDEX$]", _node);
		return p;
	}
}
