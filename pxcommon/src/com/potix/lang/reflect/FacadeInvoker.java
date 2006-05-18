/* FacadeInvoker.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommon/src/com/potix/lang/reflect/FacadeInvoker.java,v 1.2 2006/02/27 03:41:59 tomyeh Exp $
	Purpose: 
	Description: 
	History:
	2001/11/27 20:06:18, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import com.potix.lang.Classes;

/**
 * The facade invocation handler. Like the facade pattern, this invocation
 * handler is used to facade an object with a set of interfaces.
 *
 * <p>This Invocation handler assumes the object being facaded
 * (aka, the target) has all methods of these interfaces, but it might not
 * implement all of these interfaces directly.
 *
 * <p>It happens when you need to provide interfaces to classes that
 * come from third parties.
 *
 * <p>Example:<br>
 *<pre><code>class A {
 *  public void f() {...}
 *}
 *interface I {
 *  public void f();
 *}</code>
 *Then, you could do:
 *<code>I i = (I)FacadeInvoker.newInstance(new A(), new Class[] {I});</code></pre>
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:41:59 $
 */
public class FacadeInvoker implements InvocationHandler {
	/** The target that really implements the interfaces. */
	private Object _target;

	/**
	 * Creates an object that implements the giving interfaces by
	 * wrapping a giving object, traget.
	 *
	 * <p>The target must have all methods in all given interfaces,
	 * but might not implement all these interfaces.
	 */
	public static final Object newInstance
	(Object target, Class[] interfaces, ClassLoader clsLoader) {
		return Proxy.newProxyInstance(
			clsLoader, interfaces, new FacadeInvoker(target));
	}
	/**
	 * Creates an object that implements the giving interfaces by
	 * wrapping a giving object, traget.
	 * <p>A shortcut: newInstance(
	 * target, interfaces, Thread.currentThread().getContextClassLoader()).
	 */
	public static final Object newInstance(Object target, Class[] interfaces) {
		return newInstance(
			target, interfaces, Thread.currentThread().getContextClassLoader());
	}

	protected FacadeInvoker(Object target) {
		_target = target;
	}

	//-- InvocationInvoker --//
	public Object invoke(Object proxy, Method method, Object[] args)
	throws Throwable {
		Class cls = _target.getClass();
		if (!method.getDeclaringClass().isAssignableFrom(cls))
			method = Classes.getMethodInPublic(
					cls, method.getName(), method.getParameterTypes());

		try {
			return method.invoke(_target, args);
		}catch(InvocationTargetException ex) {
			throw ex.getCause();
		}
	}
}
