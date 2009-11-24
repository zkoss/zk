/* FusionInvoker.java

{{IS_NOTE

	Purpose: 

	Description: 

	History:
		Tue Nov 4 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */

package org.zkoss.lang.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * The fusion invocation handler. Like the dynamic proxy pattern, this fusion
 * invocation handler is used to wrap two or more instance into one object.
 * <p>
 * This invocation handler assumes the object being wrapped has all methods of
 * these instance (aka, targets), and it might invoke the correspond methods
 * <p>
 * It happens when you need to provide a proxy object form two or more instance.
 * <p>
 * Example:
 * 
 * <pre>
 * &lt;code&gt;class A {
 *   public void f() {...}
 * }
 * class B {
 *   public void f2() {...}
 * }
 * interface IA {
 *   public void f();
 * }
 * interface IB {
 *   public void f2();
 * }
 * &lt;/code&gt;
 * then, you could create a proxy object:
 * &lt;code&gt; Object obj = FusionInvoker.newInstance(new Object[] {A, B });
 * &lt;/code&gt;
 * then use the proxy object anywhere.
 * &lt;code&gt;
 *  IA ia = (IA) obj;
 *  ia.f();
 *  IB ib = (IB) obj;
 *  ib.f2();
 *  &lt;/code&gt;
 * </pre>
 * 
 * @author RyanWu
 * @since 3.5.2
 * */
public class FusionInvoker implements InvocationHandler, java.io.Serializable {
	private Object[] _targets;

	/** Use {@link #newInstance(Object[])} instead. */
	protected FusionInvoker(Object[] targets) {
		_targets = targets;
	}

	/** Use for only two object, see {@link #newInstance(Object[])}. */
	public static Object newInstance(Object target1, Object target2) {
		return newInstance(new Object[] { target1, target2 });
	}

	/**
	 * Creates an object that contains the all interfaces by wrapping giving
	 * object, targets.
	 * <p>
	 * Usage shortcut: FusionInvoker.newInstance(new Object[] { Object a, Object
	 * b });
	 * 
	 * @param targets
	 *            the objects need to wrapped
	 */
	public static Object newInstance(Object[] targets) {
		Set targetClasses = new HashSet();
		for (int i = 0; i < targets.length; i++) {
			Class[] allClass = targets[i].getClass().getInterfaces();
			for (int j = 0; j < allClass.length; j++) {
				targetClasses.add(allClass[j]);
			}
		}
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), (Class[]) targetClasses
				.toArray(new Class[targetClasses.size()]), new FusionInvoker(
				targets));
	}

	// -- InvocationHandler --//
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Class cls = method.getDeclaringClass();
		for (int i = 0; i < _targets.length; ++i)
			if (cls.isInstance(_targets[i]))
				return method.invoke(_targets[i], args);
		throw new InternalError("Unknown method " + method);
	}
}
