/** ProxyHelper.java.

	Purpose:

	Description:

	History:
		12:03:06 PM Dec 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.zkoss.bind.Form;
import org.zkoss.bind.annotation.Immutable;
import org.zkoss.bind.annotation.ImmutableElements;
import org.zkoss.bind.annotation.ImmutableFields;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.SystemException;
import org.zkoss.util.Pair;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.ListModelSet;

/**
 * A proxy helper class to create a proxy cache mechanism for Set, List, Collection,
 * Map, and POJO.
 * @author jumperchen
 * @since 8.0.0
 */
public class ProxyHelper {
	private static Map<Class<?>, Boolean> _ignoredClasses = new ConcurrentHashMap<Class<?>, Boolean>();
	private static Map<Class<?>, Boolean> _ignoredSuperClasses = new ConcurrentHashMap<Class<?>, Boolean>();
	private static List<ProxyTargetHandler> _proxyTargetHandlers;
	private static ProxyDecorator _proxyDecorator;

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
		List<String> superClasses = Library.getProperties("org.zkoss.bind.proxy.IgnoredSuperProxyClasses");
		if (superClasses != null && superClasses.size() != 0) {
			for (String className : superClasses) {
				try {
					addIgnoredSuperProxyClass(Classes.forNameByThread(className.trim()));
				} catch (ClassNotFoundException ex) {
					throw new SystemException("Failed to load class " + className);
				}
			}
		}
		_proxyTargetHandlers = new LinkedList<ProxyTargetHandler>(ZKProxyTargetHandlers.getSystemProxyTargetHandlers());

		String cls = Library.getProperty("org.zkoss.bind.proxy.ProxyDecoratorClass");
		if (cls != null) {
			try {
				_proxyDecorator = (ProxyDecorator) Classes.newInstanceByThread(cls.trim());
			} catch (Exception e) {
				throw new SystemException("Failed to load class " + cls);
			}
		}
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
		factory.setUseWriteReplace(false);
		if (origin instanceof ListModelList) // detect ZK ListModel First
			return (T) new ListModelListProxy((ListModelList) origin, annotations);
		else if (origin instanceof ListModelSet)
			return (T) new ListModelSetProxy((ListModelSet) origin, annotations);
		else if (origin instanceof ListModelMap)
			return (T) new ListModelMapProxy((ListModelMap) origin, annotations);
		else if (origin instanceof ListModelArray)
			return (T) new ListModelArrayProxy((ListModelArray) origin, annotations);
		else if (origin instanceof List) {
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
			return _proxyDecorator != null ? (T) _proxyDecorator.decorate((ProxyObject) p1) : (T) p1;
		}
	}

	/**
	 * Adds an ignored proxy class type. Once the data binder try to create a proxy
	 * object for the form binding, it will check whether the origin class type
	 * should be ignored.
	 * <p>Default it will apply these classes from the library property
	 * <code>org.zkoss.bind.proxy.IgnoredProxyClasses</code>
	 * </p>
	 */
	public static void addIgnoredProxyClass(Class<?> type) {
		_ignoredClasses.put(type, Boolean.TRUE);
	}

	/**
	 * Adds an ignored super proxy class type. Once the data binder try to create a proxy
	 * object for the form binding, it will check the super classes of the origin class type. If one of the super
	 * classes is in the ignored super classes, the origin class type should be ignored.
	 * <p>Default it will apply these classes from the library property
	 * <code>org.zkoss.bind.proxy.IgnoredSuperProxyClasses</code>
	 * </p>
	 * @since 8.6.1
	 */
	public static void addIgnoredSuperProxyClass(Class<?> type) {
		_ignoredSuperClasses.put(type, Boolean.TRUE);
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
		for (Map.Entry<Class<?>, Boolean> clzInfo : _ignoredSuperClasses.entrySet()) {
			if (clzInfo.getKey().isAssignableFrom(type)) {
				_ignoredClasses.put(type, Boolean.TRUE); //cache
				return true;
			}
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
		factory.setUseWriteReplace(false);
		factory.setFilter(FormProxyHandler.FORM_METHOD_FILTER);
		if (origin instanceof FormProxyObject)
			type = ((FormProxyObject) origin).getOriginObject().getClass();
		if (origin != null)
			type = getTargetClassIfProxied(origin.getClass());

		// set super class
		boolean isTypeInterface = type.isInterface();
		if (!isTypeInterface)
			factory.setSuperclass(type);

		// set interface
		int i = 0;
		int len0 = interfaces != null ? interfaces.length : 0;
		Class[] newArray = new Class[len0 + 3 + (isTypeInterface ? 1 : 0)];
		if (interfaces != null) {
			i += len0;
			System.arraycopy(interfaces, 0, newArray, 0, len0);
		}
		newArray[i++] = FormProxyObject.class;
		newArray[i++] = Form.class;
		newArray[i++] = FormFieldCleaner.class;
		if (isTypeInterface)
			newArray[i++] = type;
		factory.setInterfaces(newArray);

		Class<?> proxyClass = factory.createClass();
		Object p1 = null;
		try {
			p1 = proxyClass.newInstance();
		} catch (Exception e) {
			throw UiException.Aide.wrap(e,
					"Cannot create a proxy object:[" + origin.getClass() + "], an empty constructor is needed.");
		}

		((Proxy) p1).setHandler(new FormProxyHandler<T>(origin));
		return _proxyDecorator != null ? (T) _proxyDecorator.decorate((ProxyObject) p1) : (T) p1;
	}

	/**
	 * Internal use only.
	 */
	/* package */static void cacheSavePropertyBinding(ProxyNode node, String property, SavePropertyBinding savePropertyBinding) {
		while (node != null) {
			ProxyNode parent = node.getParent();
			if (parent == null) {
				node.getCachedSavePropertyBinding().add(new Pair<String, SavePropertyBinding>(property, savePropertyBinding));
				break;
			} else {
				String parentProperty = parent.getProperty();
				if (!property.startsWith("[") && parentProperty != null && parentProperty.length() != 0)
					parentProperty += ".";
				property = parentProperty + property;
				node = parent;
			}
		}
	}

	/**
	 * Internal use only.
	 */
	/* package */static void callOnDataChange(ProxyNode node, Object value) {
		while (node != null) {
			ProxyNode parent = node.getParent();
			if (parent == null && node.getOnDataChangeCallback() != null) {
				node.getOnDataChangeCallback().call(value);
				break;
			} else {
				node = parent;
			}
		}
	}

	/**
	 * Internal use only.
	 */
	/* package */static void callOnDirtyChange(ProxyNode node) {
		while (node != null) {
			ProxyNode parent = node.getParent();
			if (parent == null && node.getOnDirtyChangeCallback() != null) {
				node.getOnDirtyChangeCallback().call(true);
				break;
			} else {
				node = parent;
			}
		}
	}

	/**
	 * An interface to decorate the {@link ProxyObject} for some purposes, like
	 * providing custom validation logic or adding extra handler on it.
	 *
	 * <p>To specify the custom proxy decorator class, you can specify the library
	 * property <code>org.zkoss.bind.proxy.ProxyDecoratorClass</code> in zk.xml
	 * </p>
	 * since 8.0.3
	 */
	public interface ProxyDecorator {
		public ProxyObject decorate(ProxyObject proxyObject);
	}

	/**
	 * Internal use only.
	 */
	public static String toSetter(String attr) {
		return capitalize("set", attr);
	}

	/**
	 * Internal use only.
	 */
	public static String toGetter(String attr) {
		return capitalize("get", attr);
	}

	/**
	 * Internal use only.
	 */
	public static String capitalize(String prefix, String attr) {
		return new StringBuilder(prefix).append(Character.toUpperCase(attr.charAt(0))).append(attr.substring(1))
				.toString();
	}

	/**
	 * Internal use only.
	 */
	public static boolean isAttribute(Method method) {
		if (!Modifier.isPublic(method.getModifiers()))
			return false;

		final String nm = method.getName();
		final int len = nm.length();
		switch (method.getParameterTypes().length) {
			case 0:
				if (len >= 3 && nm.startsWith("is"))
					return true;
				return len >= 4 && nm.startsWith("get");
			case 1:
				return len >= 4 && nm.startsWith("set");
			default:
				return false;
		}
	}

	/**
	 * Internal use only.
	 */
	public static String toAttrName(Method method, int prefix) {
		final String name = method.getName();
		final String attrName = name.substring(prefix, name.length());
		return new StringBuilder(attrName.length()).append(Character.toLowerCase(attrName.charAt(0)))
				.append(attrName.substring(1)).toString();
	}

	/**
	 * Internal use only.
	 */
	public static String toAttrName(Method method) {
		return toAttrName(method, 3);
	}
}
