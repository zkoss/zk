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

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.ImmutableFields;
import org.zkoss.bind.annotation.Transient;
import org.zkoss.bind.impl.AllocUtil;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.UiException;

/**
 * A bean proxy handler
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public class BeanProxyHandler<T> implements MethodHandler, Serializable {
	protected static MethodFilter BEAN_METHOD_FILTER = new MethodFilter() {
		public boolean isHandled(Method m) {
			if (m.isAnnotationPresent(Transient.class))
				return false;
			final String name = m.getName();
			if ("hashCode".equals(name) || "equals".equals(name))
				return true;
			if (ProxyHelper.isAttribute(m)) {
				if (name.startsWith("set"))
					return isSetMethodHandled(m);
				if (name.startsWith("get") || name.startsWith("is"))
					return true;
			}
			try {
				FormProxyObject.class.getMethod(name, m.getParameterTypes());
				return true;
			} catch (NoSuchMethodException e) {
				return false;
			}
		}

	};
	protected T _origin;

	protected Map<String, Object> _cache;
	protected Set<String> _dirtyFieldNames; // field name that is dirty

	//ZK-3185: Enable form validation with reference and collection binding
	protected ProxyNode _node;

	public BeanProxyHandler(T origin) {
		_origin = origin;
	}

	protected static boolean isSetMethodHandled(Method m) {
		try {
			final String getter = ProxyHelper.toGetter(ProxyHelper.toAttrName(m));
			final Method getMethod = Classes.getMethodByObject(m.getDeclaringClass(), getter, null);
			if (getMethod.isAnnotationPresent(Transient.class))
				return false;
		} catch (NoSuchMethodException e) {
			// ignore if no getter available
		}
		return true;
	}

	private void addCache(String key, Object value) {
		_cache = AllocUtil.inst.putMap(_cache, key, value);
	}

	private void addDirtyField(String field) {
		_dirtyFieldNames = AllocUtil.inst.addSet(_dirtyFieldNames, field);
	}

	//ZK-3185: Enable form validation with reference and collection binding
	private void setPath(String property, ProxyNode parent) {
		if (property == null && _node != null) // means update
			_node.setParent(parent);
		else
			_node = new ProxyNodeImpl(property, parent);
	}

	public Object invoke(Object self, Method method, Method proceed, Object[] args) throws Exception {
		try {
			final String mname = method.getName();
			if (mname.equals("hashCode")) {
				int a = (_origin != null) ? (Integer) method.invoke(_origin, args) : 0;
				return 37 * 31 + a;
			}
			if (mname.equals("equals")) {
				if (_origin != null) {
					if (_origin instanceof FormProxyObject) {
						return method.invoke(((FormProxyObject) _origin).getOriginObject(), args);
					} else if (args.length == 1 && args[0] instanceof FormProxyObject) {
						return method.invoke(_origin, new Object[] { ((FormProxyObject) args[0]).getOriginObject() });
					} else {
						return method.invoke(_origin, args);
					}
				}
				// check null value
				return args.length == 1 && args[0] == null;
			}
			if (method.getDeclaringClass().isAssignableFrom(FormProxyObject.class)) {
				if ("submitToOrigin".equals(mname)) {
					if (_origin != null && _cache != null) {
						for (Map.Entry<String, Object> me : _cache.entrySet()) {
							Object value = me.getValue();
							if (value instanceof FormProxyObject) {
								((FormProxyObject) value).submitToOrigin((BindContext) args[0]);
								value = ((FormProxyObject) value).getOriginObject();
							}

							if (_dirtyFieldNames != null && _dirtyFieldNames.contains(me.getKey())) {
								final String setter = ProxyHelper.toSetter(me.getKey());
								try {
									final Method m = Classes.getMethodByObject(_origin.getClass(), setter,
											new Object[] { value });
									m.invoke(_origin, Classes.coerce(m.getParameterTypes()[0], value));
									BindELContext.addNotifys(m, _origin, me.getKey(), value, (BindContext) args[0]);
								} catch (NoSuchMethodException e) {
									throw UiException.Aide.wrap(e);
								}
							}
						}
						if (_dirtyFieldNames != null)
							_dirtyFieldNames.clear();
					}
				} else if ("getOriginObject".equals(mname)) {
					return _origin;
				} else if ("resetFromOrigin".equals(mname)) {
					if (_dirtyFieldNames != null)
						_dirtyFieldNames.clear();
					if (_cache != null)
						_cache.clear();
				} else if ("isFormDirty".equals(mname)) {
					boolean dirty = false;

					if (_cache != null) {
						// If the dirty field is a form proxy object it may not be dirty.
						// But once it contains a non-form proxy object, it must be dirty.
						for (Map.Entry<String, Object> me : _cache.entrySet()) {
							final Object value = me.getValue();
							if (_dirtyFieldNames != null && _dirtyFieldNames.contains(me.getKey())) {
								dirty = true;
								break;
							}

							if (value instanceof FormProxyObject) {
								if (((FormProxyObject) value).isFormDirty()) {
									dirty = true;
									break;
								}
							}
						}
					}
					return dirty;
				} else if ("setPath".equals(mname)) {
					//ZK-3185: Enable form validation with reference and collection binding
					setPath((String) args[0], (ProxyNode) args[1]);
				} else if ("cacheSavePropertyBinding".equals(mname)) {
					//ZK-3185: Enable form validation with reference and collection binding
					ProxyHelper.cacheSavePropertyBinding(_node, _node.getProperty() + "." + (String) args[0], (SavePropertyBinding) args[1]);
					return null;
				} else {
					throw new IllegalAccessError("Not implemented yet for FormProxyObject interface: [" + mname + "]");
				}
			} else {
				if (mname.startsWith("get")) {
					if (_origin == null)
						return null;

					final String attr = ProxyHelper.toAttrName(method);
					if (_cache != null) {
						if (_cache.containsKey(attr)) {
							Object cacheData = _cache.get(attr);
							if (_origin.getClass().getAnnotation(ImmutableFields.class) == null
									&& !(self instanceof ImmutableFields)) {
								// ZK-2736 Form proxy with Immutable values
								Object proxyIfAny = ProxyHelper.createProxyIfAny(
										cacheData, method.getAnnotations());

								addCache(attr, proxyIfAny);

								return proxyIfAny;
							}
							return cacheData;
						}
					}

					Object value = method.invoke(_origin, args);
					if (value != null) {
						if (_origin.getClass().getAnnotation(ImmutableFields.class) == null
								&& !(self instanceof ImmutableFields)) {
							// ZK-2736 Form proxy with Immutable values
							value = ProxyHelper.createProxyIfAny(value, method.getAnnotations());
						}

						addCache(attr, value);
						if (value instanceof FormProxyObject) //ZK-3185: Enable form validation with reference and collection binding
							((FormProxyObject) value).setPath(attr, _node);
					}
					return value;
				} else if (mname.startsWith("is")) {
					if (_origin == null)
						return false;

					final String attr = ProxyHelper.toAttrName(method, 2);
					if (_cache != null) {
						if (_cache.containsKey(attr)) {
							return _cache.get(attr);
						}
					}
					return method.invoke(_origin, args);
				} else {
					final String attrName = ProxyHelper.toAttrName(method);

					addCache(attrName, args[0]);
					addDirtyField(attrName);
					ProxyHelper.callOnDataChange(_node, new Object[]{self, attrName});
					ProxyHelper.callOnDirtyChange(_node);
				}
			}
		} catch (Exception e) {
			throw UiException.Aide.wrap(e);
		}
		return null;
	}
}
