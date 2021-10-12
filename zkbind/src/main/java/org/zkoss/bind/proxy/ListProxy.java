/** ListProxy.java.

	Purpose:
		
	Description:
		
	History:
		2:10:51 PM Dec 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.proxy;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * A list proxy
 * @author jumperchen
 * @since 8.0.0
 */
public class ListProxy<E> extends AbstractCollectionProxy<E> implements List<E> {
	private static final long serialVersionUID = 20141225150833L;
	private Annotation[] _callerAnnots;

	public ListProxy(Collection<E> origin, Annotation[] callerAnnots) {
		super(origin, callerAnnots);
		_callerAnnots = callerAnnots;
		resetFromOrigin();
	}

	@SuppressWarnings("unchecked")
	protected Collection<E> initCache() {
		return new ArrayList<E>(((Collection<E>) getOriginObject()).size());
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		boolean result = false;
		if (c.size() > 0) {
			List<E> proxyList = new LinkedList<>();
			for (E e : c)
				proxyList.add(createProxyObject(e));
			result = ((List<E>) getCache()).addAll(index, proxyList);
			setDirty(true);
		}
		return result;
	}

	public E get(int index) {
		return ((List<E>) getCache()).get(index);
	}

	public E set(int index, E element) {
		E prevElement = ((List<E>) getCache()).set(index, createProxyObject(element));
		setDirty(true);
		return prevElement;
	}

	public void add(int index, E element) {
		((List<E>) getCache()).add(index, createProxyObject(element));
		setDirty(true);
	}

	public E remove(int index) {
		E removed = ((List<E>) getCache()).remove(index);
		setDirty(true);
		return removed;
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
		return ((List<E>) getCache()).listIterator();
	}

	public ListIterator<E> listIterator(int index) {
		return ((List<E>) getCache()).listIterator(index);
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return ProxyHelper.createProxyIfAny(((List<E>) getCache()).subList(fromIndex, toIndex), _callerAnnots);
	}

}
