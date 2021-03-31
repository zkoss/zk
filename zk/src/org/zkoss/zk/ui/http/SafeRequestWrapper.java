/* SafeRequestWrapper.java

	Purpose:

	Description:

	History:
		Thu Mar 25 09:52:08 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.http;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Primitives;

/**
 * A safe wrapper for request that avoids exceptions on a recycled request.
 * (e.g. a completed asynchronous servlet request)
 *
 * @author rudyhuang
 * @since 9.6.0
 */
class SafeRequestWrapper implements InvocationHandler {
	private static final Logger LOG = LoggerFactory.getLogger(SafeRequestWrapper.class);

	private final HttpServletRequest _request;
	private boolean _isVoided;

	static HttpServletRequest wrap(HttpServletRequest request) {
		if (request == null)
			return null;

		return (HttpServletRequest) Proxy.newProxyInstance(
				request.getClass().getClassLoader(),
				new Class<?>[]{ HttpServletRequest.class },
				new SafeRequestWrapper(request));
	}

	private SafeRequestWrapper(HttpServletRequest request) {
		this._request = request;
	}

	HttpServletRequest getOriginObject() {
		return _request;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final Class<?> returnType = method.getReturnType();
		if (_isVoided)
			return getDefaultValue(returnType);

		try {
			return method.invoke(_request, args);
		} catch (InvocationTargetException e) {
			final Throwable cause = e.getCause();
			// To catch a NPE on a Tomcat recycled request
			// https://github.com/apache/tomcat/blob/9.0.44/java/org/apache/catalina/connector/Request.java#L1653
			final boolean isRemoveAttributeNPE = "removeAttribute".equals(method.getName())
					&& cause instanceof NullPointerException;
			if (isRemoveAttributeNPE || cause instanceof IllegalStateException) {
				LOG.warn("Illegal request state, an exception was caught.", cause);
				_isVoided = true;
				return getDefaultValue(returnType);
			}
			throw e; // throw others
		}
	}

	private Object getDefaultValue(Class<?> type) {
		if (type.isPrimitive())
			return Primitives.getDefaultValue(type);
		if (Classes.isPrimitiveWrapper(type))
			return Primitives.getDefaultValue(Primitives.toPrimitive(type));
		return null;
	}
}
