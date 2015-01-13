/** ProxyHelper.java.

	Purpose:
		
	Description:
		
	History:
		12:03:06 PM Dec 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.proxy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import org.zkoss.bind.Form;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.zk.ui.UiException;

/**
 * A proxy helper class to create a proxy cache mechanism for Set, List, Collection,
 * Map, and POJO.
 * @author jumperchen
 * @since 8.0.0
 */
public class ProxyHelper {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Object> T createProxyIfAny(T origin) {
		
		if (origin == null)
			return null;
		if (origin instanceof FormProxyObject) {
			return origin;
		}
		if (BindELContext.isImmutable(origin))
			return origin;
		
		ProxyFactory factory = new ProxyFactory();
		if (origin instanceof List) {
			return (T) new ListProxy((List)origin);
		} else if (origin instanceof Set) {
			return (T) new SetProxy((Set)origin);
		} else if (origin instanceof Map) {
			return (T) new MapProxy((Map)origin);
		} else if (origin instanceof Collection) {
			return (T) new ListProxy((Collection)origin);
		} else if (origin.getClass().isArray()) {
			throw new UnsupportedOperationException("Array cannot be a proxy object!");
		} else {
			factory.setFilter(BeanProxyHandler.BEAN_METHOD_FILTER);
			factory.setSuperclass(origin.getClass());
			factory.setInterfaces(new Class[]{FormProxyObject.class});
			Class<?> proxyClass = factory.createClass();
			Object p1 = null;
			try {
				p1 = proxyClass.newInstance();
			} catch (Exception e) {
				throw new UiException("Cannot create a proxy object:[" + origin.getClass() + "], an empty constructor is needed.", e);
			}

			((Proxy) p1).setHandler(new BeanProxyHandler<T>(origin));
			return (T) p1;
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Object> T createFormProxy(T origin, Class<?> type) {

		ProxyFactory factory = new ProxyFactory();
		factory.setFilter(FormProxyHandler.FORM_METHOD_FILTER);
		factory.setSuperclass(type);
		factory.setInterfaces(new Class[]{FormProxyObject.class, Form.class});
		Class<?> proxyClass = factory.createClass();
		Object p1 = null;
		try {
			p1 = proxyClass.newInstance();
		} catch (Exception e) {
			throw new UiException("Cannot create a proxy object:[" + origin.getClass() + "], an empty constructor is needed.", e);
		}

		((Proxy) p1).setHandler(new FormProxyHandler<T>(origin));
		return (T) p1;
	}
}
