/* ClassUtil.java

	History:
		Fri, Sep 27, 2013 10:41:21 PM, Created by tomyeh

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zel.impl.util;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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
		return _classReflect.newInstance(cls, args);
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
	/**
	 * Returns the context ClassLoader for the reference class.
	 * <p>Default: returns from the current thread.
	 * <br/>
	 * Or specify the library property of <code>org.zkoss.lang.contextClassLoader.class</code>
	 * in zk.xml to provide a customized class loader.
	 * </p>
	 * @param reference the reference class where it is invoked from.
	 * @since 8.0.2
	 */
	public static ClassLoader getContextClassLoader(Class<?> reference) {
		return _classReflect.getContextClassLoader(reference);
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
    
    //ZK-2419
    public static boolean isInstance(Object value, Class<?> clz) {
    	if (clz.isPrimitive()) {
    		clz = _primToClass.get(clz);
    	}
    	return clz.isInstance(value);
    }
    
    private static Map<Class<?>, Class<?>> _primToClass = new HashMap<Class<?>, Class<?>>(8 * 4 / 3); 
    static {
    	_primToClass.put(boolean.class, Boolean.class);
    	_primToClass.put(byte.class, Byte.class);
    	_primToClass.put(char.class, Character.class);
    	_primToClass.put(short.class, Short.class);
    	_primToClass.put(int.class, Integer.class);
    	_primToClass.put(long.class, Long.class);
    	_primToClass.put(float.class, Float.class);
    	_primToClass.put(double.class, Double.class);
    }
}
