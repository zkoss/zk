/* MethodFunction.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 12:11:21     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.util;

import java.lang.reflect.Method;

import org.zkoss.lang.reflect.SerializableMethod;
import org.zkoss.xel.Function;

/**
 * A XEL function based on java.lang.reflect.Method.
 * It is used by implementation of {@link org.zkoss.xel.ExpressionFactory}
 * The user of XEL expressions rarely need it.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class MethodFunction extends SerializableMethod implements Function {
	public MethodFunction(Method method) {
		super(method);
	}

	public Class<?>[] getParameterTypes() {
		return getMethod().getParameterTypes();
	}
	public Class<?> getReturnType() {
		return getMethod().getReturnType();
	}
	public Object invoke(Object obj, Object... args) throws Exception {
		return getMethod().invoke(obj, args);
	}
	public Method toMethod() {
		return getMethod();
	}
}
