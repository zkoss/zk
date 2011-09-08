/* FusionInvoker.java


	Purpose: 

	Description: 

	History:
		Tue Nov 4 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
 * The fusion invocation handler. It is used to <i>fuse</i>
 * two or more instance into one object.
 * It is usefully if you want to have a single object to represent two more
 * other instances.
 *
 * <p>Example:
 * 
 * <pre><code>
 * public interface IA {
 *   public void f();
 * }
 * public interface IB {
 *   public void g();
 * }
 * public class A implements IA {
 *   public void f() {...}
 * }
 * public class B implements IB {
 *   public void g() {...}
 * }
 * </code></pre>
 * Then, you could fuse them together as follows:
 * <pre><code>
 * Object obj = FusionInvoker.newInstance(new Object[] {new A(), new B()});
 * </code></pre>
 *
 * Thus, the fused proxy object, <code>obj</code>, could be used as if
 * it implements <code>IA</code> and <code>IB</code>:
 *
 * <pre><code>
 *  IA ia = (IA) obj;
 *  ia.f();
 *  IB ib = (IB) obj;
 *  ib.g();
 * </code></pre>
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
		Set<Class> targetClasses = new HashSet<Class>();
		for (int i = 0; i < targets.length; i++) {
			Class[] allClass = targets[i].getClass().getInterfaces();
			for (int j = 0; j < allClass.length; j++) {
				targetClasses.add(allClass[j]);
			}
		}
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return Proxy.newProxyInstance(
			cl != null ? cl: FusionInvoker.class.getClassLoader(),
			(Class[]) targetClasses
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
