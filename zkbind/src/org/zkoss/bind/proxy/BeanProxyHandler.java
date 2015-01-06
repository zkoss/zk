/** BeanProxyHandler.java.

	Purpose:
		
	Description:
		
	History:
		12:16:01 PM Dec 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.impl.AllocUtil;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.zk.ui.UiException;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;

/**
 * A bean proxy handler
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public class BeanProxyHandler<T> implements MethodHandler, Serializable {
	protected static MethodFilter BEAN_METHOD_FILTER = new MethodFilter() {
		public boolean isHandled(Method m) {
			if (m.getName().startsWith("set")
					|| m.getName().startsWith("get")
					|| m.getName().equals("hashCode"))
				return true;
			try {
				FormProxyObject.class.getMethod(m.getName(), m.getParameterTypes());
			    return true;
			} catch (NoSuchMethodException e) {
			    return false;
			}
		}

	};
	private T _origin;

	private Map<String, Object> _cache;
	private Set<String> _dirtyFieldNames; // field name that is dirty

	public BeanProxyHandler(T origin) {
		_origin = origin;
	}

	private void addCache(String key, Object value) {
		_cache = AllocUtil.inst.putMap(_cache, key, value);
	}

	private void addDirtyField(String field) {
		_dirtyFieldNames = AllocUtil.inst.addSet(_dirtyFieldNames, field);
	}

	public Object invoke(Object self, Method method, Method proceed,
			Object[] args) throws Exception {
		try {
			if (method.getName().equals("hashCode")) {
				int a = (Integer) method.invoke(_origin, args);
				return 37 * 31 + a; 
			}
			if (method.getDeclaringClass().isAssignableFrom(
					FormProxyObject.class)) {
				if ("submitToOrigin".equals(method.getName())) {
					if (_dirtyFieldNames != null) {
						for (String field : _dirtyFieldNames) {
							final Object value = _cache.get(field);
							if (value instanceof FormProxyObject) {
								((FormProxyObject) value)
										.submitToOrigin((BindContext) args[0]);
							} else {
								final String mname = toSetter(field);
								try {
									final Method m = _origin.getClass()
											.getMethod(
													mname,
													new Class[] { value
															.getClass() });
									m.invoke(_origin, value);
									BindELContext.addNotifys(m, _origin, field,
											value, (BindContext) args[0]);
								} catch (NoSuchMethodException e) {
									throw new UiException(e);
								}
							}
						}
						_dirtyFieldNames.clear();
					}
				} else if ("getOriginObject".equals(method.getName())) {
					return _origin;
				} else if ("resetFromOrigin".equals(method.getName())) {
					if (_dirtyFieldNames != null)
						_dirtyFieldNames.clear();
					if (_cache != null)
						_cache.clear();
				} else if ("isDirtyForm".equals(method.getName())) {
					if (_dirtyFieldNames == null || _dirtyFieldNames.isEmpty()) {
						return false;
					} else {
						boolean dirty = false;
						
						// If the dirty field is a form proxy object it may not be dirty.
						// But once it contains a non-form proxy object, it must be dirty.
						for (String field : _dirtyFieldNames) {
							final Object value = _cache.get(field);
							if (value instanceof FormProxyObject) {
								if (((FormProxyObject) value).isDirtyForm()) {
									dirty = true;
									break;
								}
							} else {
								dirty = true;
								break;
							}
						}
						return dirty;
					}
				} else {
					throw new IllegalAccessError("Not implemented yet for FormProxyObject interface: [" + method.getName() + "]");
				}
			} else {
				if (method.getName().startsWith("get")) {
					final String attr = toAttrName(method);
					Object value = null;
					if (_cache == null) {
						Object invoke = method.invoke(_origin, args);
						if (invoke != null) {
							value = ProxyHelper.createProxyIfAny(invoke);
							addCache(attr, value);
							if (value instanceof FormProxyObject) {
								addDirtyField(attr); // it may be changed.
							}
						}
					} else {
						value = _cache.get(attr);
						if (!_cache.containsKey(attr)) {
							Object invoke = method.invoke(_origin, args);
							if (invoke != null) {
								value = ProxyHelper.createProxyIfAny(invoke);
								addCache(attr, value);
								if (value instanceof FormProxyObject) {
									addDirtyField(attr); // it may be changed.
								}
							}
						}
					}
					return value;
				} else {
					String attrName = toAttrName(method);
					addCache(attrName, args[0]);
					addDirtyField(attrName);
				}
			}
		} catch (Exception e) {
			throw new UiException(e);
		}
		return null;
	}

	private static String toSetter(String attr) {
		return capitalize("set", attr);
	}

	private static String toGetter(String attr) {
		return capitalize("get", attr);
	}

	private static String capitalize(String prefix, String attr) {
		return new StringBuilder(prefix)
				.append(Character.toUpperCase(attr.charAt(0)))
				.append(attr.substring(1)).toString();
	}

	private static String toAttrName(Method method) {
		final String name = method.getName();
		return name.toLowerCase().substring(3, name.length());
	}
}
