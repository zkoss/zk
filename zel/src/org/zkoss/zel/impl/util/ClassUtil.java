/* ClassUtil.java

	History:
		Fri, Sep 27, 2013 10:41:21 PM, Created by tomyeh

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zel.impl.util;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.zkoss.zel.ELException;

/**
 * Class related utilities.
 *
 * @author tomyeh
 */
public class ClassUtil {
	/**
	 * Gets one of the close methods -- a close method is a method
	 * with the same name and the compatible argument type.
	 */
	public static final Method
	getCloseMethod(Class<?> cls, String name, Class<?>[] argTypes)
	throws NoSuchMethodException {
		return _classReflect.getCloseMethod(cls, name, argTypes);
	}

	/**
	 * Instantiates a new instance of the specified class with the
	 * specified argument.
	 */
	public static final
	Object newInstance(Class<?> cls, Object[] args)
	throws NoSuchMethodException, InstantiationException,
	InvocationTargetException, IllegalAccessException {
		return newInstance(cls, args);
	}

	/**
	 * Returns the Class object of the specified class name, using
	 * the current thread's context class loader.
	 */
	public static final Class<?> forNameByThread(String clsName)
	throws ClassNotFoundException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl != null)
			try {
				return Class.forName(clsName, true, cl);
			} catch (ClassNotFoundException ex) { //ignore and try the other
			}
		return ClassUtil.class.forName(clsName);
	}

	private static ClassReflect _classReflect;
	static {
		final String className = "org.zkoss.xel.zel.ClassReflect";
		try {
			_classReflect = (ClassReflect)forNameByThread(className).newInstance();
		} catch (Throwable ex) {
			throw new ELException("Unable to load "+className+". Make sure zcommon.jar is available.", ex);
		}
	}
}
