/** ListModelListProxy.java.

	Purpose:

	Description:

	History:
		Mon Jun 08 11:30:22 CST 2021, Created by jameschu

	Copyright (C) 2021 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.PagingListener;
import org.zkoss.zul.ext.SelectionControl;
import org.zkoss.zul.ext.Sortable;

/**
 * A ListModelList Proxy
 *
 * @author jameschu
 * @since 9.6.0
 */
public class ListModelListProxy<E> extends ListModelList<E> implements Proxy, FormProxyObject, Serializable {
	private static final long serialVersionUID = 20210608113022L;
	private ListModelList<E> _cache;
	private ListModelList<E> _origin;
	private boolean _dirty;
	private boolean isImmutableElements;
	private Annotation[] _callerAnnots;
	private ProxyNode _node;

	public ListModelListProxy(ListModelList<E> origin, Annotation[] callerAnnots) {
		_origin = origin;
		_cache = initCache();
		if (callerAnnots != null) {
			for (Annotation annot : callerAnnots) {
				if (annot.annotationType().isAssignableFrom(ImmutableElements.class)) {
					isImmutableElements = true;
					break;
				}
			}
			_callerAnnots = callerAnnots;
		}
		resetFromOrigin();
	}

	private ListModelList<E> initCache() {
		ListModelList<E> cache = new ListModelList<>(getOriginObject().size());
		return cache;
	}

	public ListModelList<E> getCache() {
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

	public void setSelectionControl(SelectionControl ctrl) {
		getCache().setSelectionControl(ctrl); //initial setting
	}

	public SelectionControl getSelectionControl() {
		return getCache().getSelectionControl(); //initial setting
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

	//-- ListModelList --//
	public void removeRange(int fromIndex, int toIndex) {
		getCache().removeRange(fromIndex, toIndex);
		setDirty(true);
	}

	public List<E> getInnerList() {
		return getCache().getInnerList();
	}

	public int getSize() {
		return getCache().getSize();
	}

	public E getElementAt(int j) {
		return getCache().getElementAt(j);
	}

	public boolean add(E o) {
		if (getCache().add(createProxyObject(o))) {
			setDirty(true);
			return true;
		}
		return false;
	}

	public void add(int index, E element) {
		getCache().add(index, createProxyObject(element));
		setDirty(true);
	}

	public boolean notifyChange(E element) {
		return getCache().notifyChange(element);
	}

	public boolean addAll(Collection<? extends E> c) {
		boolean modified = false;
		Iterator<? extends E> e = c.iterator();
		while (e.hasNext()) {
			if (add(e.next()))
				modified = true;
		}
		if (modified)
			setDirty(true);
		return modified;
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		boolean result = false;
		if (c.size() > 0) {
			List<E> proxyList = new LinkedList<>();
			for (E e : c)
				proxyList.add(createProxyObject(e));
			result = getCache().addAll(index, proxyList);
			setDirty(true);
		}
		return result;
	}

	public void clear() {
		getCache().clear();
		setDirty(true);
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

	public boolean containsAll(Collection<?> c) {
		Iterator<?> e = c.iterator();
		while (e.hasNext())
			if (!contains(e.next()))
				return false;
		return true;
	}

	public E get(int index) {
		return getCache().get(index);
	}

	public int indexOf(Object o) {
		final int size = size();
		if (o == null) {
			for (int i = 0; i < size; i++)
				if (testEquals(get(i), null))
					return i;
		} else {
			for (int i = 0; i < size; i++)
				if (testEquals(o, get(i)))
					return i;
		}
		return -1;
	}

	public boolean isEmpty() {
		return getCache().isEmpty();
	}

	public int lastIndexOf(Object o) {
		final int size = size();
		if (o == null) {
			for (int i = size - 1; i >= 0; i--)
				if (testEquals(get(i), null))
					return i;
		} else {
			for (int i = size - 1; i >= 0; i--)
				if (testEquals(o, get(i)))
					return i;
		}
		return -1;
	}

	public ListIterator<E> listIterator() {
		return getCache().listIterator();
	}

	public ListIterator<E> listIterator(int index) {
		return getCache().listIterator(index);
	}

	public boolean remove(Object o) {
		if (getCache().remove(createProxyObject(o))) {
			setDirty(true);
			return true;
		}
		return false;
	}

	public E remove(int index) {
		E removed = getCache().remove(index);
		setDirty(true);
		return removed;
	}

	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		Iterator<?> e = iterator();
		c = createProxyObject(c); // use a proxy object to compare
		while (e.hasNext()) {
			if (c.contains(e.next())) {
				e.remove();
				modified = true;
			}
		}
		if (modified)
			setDirty(true);
		return modified;
	}

	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		Iterator<E> e = iterator();
		c = createProxyObject(c); // use a proxy object to compare
		while (e.hasNext()) {
			if (!c.contains(e.next())) {
				e.remove();
				modified = true;
			}
		}
		if (modified)
			setDirty(true);
		return modified;
	}

	public Iterator<E> iterator() {
		return getCache().iterator();
	}

	public E set(int index, E element) {
		E prevElement = getCache().set(index, createProxyObject(element));
		setDirty(true);
		return prevElement;
	}

	public int size() {
		return getCache().size();
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return ProxyHelper.createProxyIfAny(getCache().subList(fromIndex, toIndex), _callerAnnots);
	}

	public Object[] toArray() {
		return getCache().toArray();
	}

	public <T> T[] toArray(T[] a) {
		return getCache().toArray(a);
	}

	//-- Object --//
	public boolean equals(Object o) {
		return getCache().equals(o);
	}

	public int hashCode() {
		return getCache().hashCode();
	}

	public String toString() {
		return getCache().toString();
	}

	//-- Sortable --//
	public void sort(Comparator<E> cmpr, final boolean ascending) {
		((Sortable) getCache()).sort(cmpr, ascending);
		setDirty(true);
	}

	public void sort() {
		((Sortable) getCache()).sort();
		setDirty(true);
	}

	public String getSortDirection(Comparator<E> cmpr) {
		return ((Sortable) getCache()).getSortDirection(cmpr);
	}

	public Object clone() {
		return getCache().clone();
	}

	//-- Proxy --//
	public void setHandler(MethodHandler mi) {
		throw new UnsupportedOperationException("Not support!");
	}

	//-- FormProxyObject --//
	public ListModelList<E> getOriginObject() {
		return _origin;
	}
	public void resetFromOrigin() {
		getCache().clear();
		setDirty(false);
		for (E e : getOriginObject()) {
			getCache().add(createProxyObject(e));
		}
	}

	public void submitToOrigin(BindContext ctx) {
		Collection<E> origin = ((Collection<E>) getOriginObject());
		origin.clear();
		for (E e : _cache) {
			if (e instanceof FormProxyObject) {
				FormProxyObject proxy = (FormProxyObject) e;
				proxy.submitToOrigin(ctx);
				origin.add((E) proxy.getOriginObject());
			} else {
				origin.add(e);
			}
		}
		setDirty(false);
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

	public void setFormOwner(Object owner, FormBinding binding) {
		throw new IllegalAccessError("Not supported");
	}

	public void setPath(String property, ProxyNode parent) {
		if (property == null && _node != null) { // means update
			_node.setParent(parent);
		} else {
			_node = new ProxyNodeImpl(property, parent);
			for (E e : _cache) {
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
