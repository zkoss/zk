/* ClassReflect.java

	History:
		Fri, Sep 27, 2013 10:41:58 PM, Created by tomyeh

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zel.impl.util;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * The implementation of {@link ClassUtil}.
 *
 * @author tomyeh
 */
public interface ClassReflect {
	/**
	 * Gets one of the close methods -- a close method is a method
	 * with the same name and the compatible argument type.
	 */
	public Method
	getCloseMethod(Class<?> cls, String name, Class<?>[] argTypes)
	throws NoSuchMethodException;

	/**
	 * Instantiates a new instance of the specified class with the
	 * specified argument.
	 */
	public Object newInstance(Class<?> cls, Object[] args)
	throws NoSuchMethodException, InstantiationException,
	InvocationTargetException, IllegalAccessException;
}
