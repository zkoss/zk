/** ProxyHelper.java.

	Purpose:
		
	Description:
		
	History:
		12:03:06 PM Dec 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.proxy;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javassist.Modifier;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import org.zkoss.bind.Form;
import org.zkoss.bind.annotation.Immutable;
import org.zkoss.bind.annotation.ImmutableElements;
import org.zkoss.bind.annotation.ImmutableFields;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.SystemException;
import org.zkoss.zk.ui.UiException;

/**
 * A proxy helper class to create a proxy cache mechanism for Set, List, Collection,
 * Map, and POJO.
 * @author jumperchen
 * @since 8.0.0
 */
public class ProxyHelper {
	private static Map<Class<?>, Boolean> _ignoredClasses = new ConcurrentHashMap<Class<?>, Boolean>();
	private static List<ProxyTargetHandler> _proxyTargetHandlers;

	static {
		List<String> classes = Library.getProperties("org.zkoss.bind.proxy.IgnoredProxyClasses");
		if (classes != null && classes.size() != 0) {
			for (String className : classes) {
				try {
					addIgnoredProxyClass(Classes.forNameByThread(className.trim()));
				} catch (ClassNotFoundException ex) {
					throw new SystemException("Failed to load class " + className);
				}
			}
		}
		_proxyTargetHandlers = new LinkedList<ProxyTargetHandler>(ZKProxyTargetHandlers.getSystemProxyTargetHandlers());
	}
	/**
	 * Creates a proxy object from the given origin object, if any.
	 * @param origin
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Object> T createProxyIfAny(T origin) {
		return createProxyIfAny(origin, null);
	}

	/**
	 * Creates a proxy object from the given origin object, if any.
	 * @param origin
	 * @param annotations the annotations of the caller method to indicate whether
	 * the elements of the collection or Map type can proxy deeply, if any. (Optional)
	 * Like {@link ImmutableElements}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Object> T createProxyIfAny(T origin, Annotation[] annotations) {
		if (origin == null)
			return null;
		if (origin instanceof FormProxyObject) {
			return origin;
		}

		origin = getOriginObject(origin);
		boolean hasImmutableFields = false;
		if (annotations != null) {
			for (Annotation annot : annotations) {
				if (annot.annotationType().isAssignableFrom(Immutable.class))
					return origin;
				if (annot.annotationType().isAssignableFrom(ImmutableFields.class))
					hasImmutableFields = true;
			}
		}
		if (isImmutable(origin))
			return origin;

		ProxyFactory factory = new ProxyFactory();
		if (origin instanceof List) {
			return (T) new ListProxy((List) origin, annotations);
		} else if (origin instanceof Set) {
			return (T) new SetProxy((Set) origin, annotations);
		} else if (origin instanceof Map) {
			return (T) new MapProxy((Map) origin, annotations);
		} else if (origin instanceof Collection) {
			return (T) new ListProxy((Collection) origin, annotations);
		} else if (origin.getClass().isArray()) {
			throw new UnsupportedOperationException("Array cannot be a proxy object!");
		} else {
			factory.setFilter(BeanProxyHandler.BEAN_METHOD_FILTER);
			factory.setSuperclass(getTargetClassIfProxied(origin.getClass()));
			if (hasImmutableFields) {
				factory.setInterfaces(new Class[] { FormProxyObject.class, ImmutableFields.class });
			} else {
				factory.setInterfaces(new Class[] { FormProxyObject.class });
			}
			Class<?> proxyClass = factory.createClass();
			Object p1 = null;
			try {
				p1 = proxyClass.newInstance();
			} catch (Exception e) {
				throw UiException.Aide.wrap(e,
						"Cannot create a proxy object:[" + origin.getClass() + "], an empty constructor is needed.");
			}

			((Proxy) p1).setHandler(new BeanProxyHandler<T>(origin));
			return (T) p1;
		}
	}

	/**
	 * Adds an ignored proxy class type. Once the data binder try to create a proxy
	 * object for the form binding, it will check whether the origin class type
	 * should be ignored.
	 */
	public static void addIgnoredProxyClass(Class<?> type) {
		_ignoredClasses.put(type, Boolean.TRUE);
	}

	/**
	 * Returns whether the given origin object is immutable.
	 */
	public static boolean isImmutable(Object origin) {
		if (BindELContext.isImmutable(origin))
			return true;

		return checkImmutable(origin.getClass());
	}

	/**
	 * Checks if the target class is already proxied
	 */
	private static <T> Class<? extends Object> getTargetClassIfProxied(Class<T> clazz) {
		if (ProxyFactory.isProxyClass(clazz))
			clazz = (Class<T>) clazz.getSuperclass();
		return clazz;
	}

	/**
	 * Internal use only.
	 */
	public static <T extends Object> T getOriginObject(T origin) {
		for (ProxyTargetHandler handlers : _proxyTargetHandlers) {
			if (handlers != null) {
				origin = handlers.getOriginObject(origin);
			}
		}
		return origin;
	}

	private static boolean checkImmutable(Class<?> type) {
		if (_ignoredClasses.containsKey(type))
			return true;
		if (Modifier.isFinal(type.getModifiers())) {
			_ignoredClasses.put(type, Boolean.TRUE);
			return true;
		}

		return false;
	}

	/**
	 * Creates a proxy form object from the given origin object, if any.
	 * @param origin the origin data object
	 * @param type the class type of the data object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Object> T createFormProxy(T origin, Class<?> type) {
		return createFormProxy(origin, type, null);
	}

	/**
	 * Creates a proxy form object from the given origin object, if any.
	 * @param origin the origin data object
	 * @param type the class type of the data object
	 * @param interfaces the interface type of the data object, if any.
	 * @since 8.0.1
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Object> T createFormProxy(T origin, Class<?> type, Class[] interfaces) {

		if (origin instanceof Form)
			return origin;
		origin = getOriginObject(origin);
		ProxyFactory factory = new ProxyFactory();
		factory.setFilter(FormProxyHandler.FORM_METHOD_FILTER);
		if (origin instanceof FormProxyObject)
			type = ((FormProxyObject) origin).getOriginObject().getClass();
		else if (origin != null)
			type = getTargetClassIfProxied(origin.getClass());
		factory.setSuperclass(type);
		if (interfaces == null) {
			factory.setInterfaces(new Class[] { FormProxyObject.class, Form.class, FormFieldCleaner.class });
		} else {
			int len0 = interfaces.length;
			Class[] newArray = new Class[len0 + 3];
			System.arraycopy(interfaces, 0, newArray, 0, len0);
			newArray[len0] = FormProxyObject.class;
			newArray[len0 + 1] = Form.class;
			newArray[len0 + 2] = FormFieldCleaner.class;
			factory.setInterfaces(newArray);
		}

		Class<?> proxyClass = factory.createClass();
		Object p1 = null;
		try {
			p1 = proxyClass.newInstance();
		} catch (Exception e) {
			throw UiException.Aide.wrap(e,
					"Cannot create a proxy object:[" + origin.getClass() + "], an empty constructor is needed.");
		}

		((Proxy) p1).setHandler(new FormProxyHandler<T>(origin));
		return (T) p1;
	}
}
