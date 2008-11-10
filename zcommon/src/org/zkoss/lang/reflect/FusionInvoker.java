/* FacadeInvoker.java

{{IS_NOTE

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
/**
 * @author Ryan Wu
 * @since 3.5.2
 */
package org.zkoss.lang.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FusionInvoker implements InvocationHandler {
	private Object _target;
	private static Set _targetClass = new HashSet();

	public FusionInvoker() {
	}

	protected FusionInvoker(Object target) {
		_target = target;
	}

	public static Object newInstance(Object target) {
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), target.getClass().getInterfaces(),
				new FusionInvoker(target));
	}

	public static Object newInstance(Object[] targets) {
		Set _targets = new HashSet();
		for (int i = 0; i < targets.length; i++) {
			_targetClass.add(targets[i]);
			Class[] allClass = targets[i].getClass().getInterfaces();
			for (int j = 0; j < allClass.length; j++) {
				_targets.add(allClass[j]);
			}
		}
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), (Class[]) _targets
				.toArray(new Class[] {}), new FusionInvoker(targets[0]));
	}

	// -- InvocationInvoker --//
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		try {
			System.out.println("1--->" + method.toString());
			Class cls = _target.getClass();

			if (!method.getDeclaringClass().isAssignableFrom(cls)) {
				System.out.println("--> not found");
				Iterator itr = _targetClass.iterator();

				while (itr.hasNext()) {
					Class _cls = itr.next().getClass();
					if (method.getDeclaringClass().isAssignableFrom(_cls)) {												
			            Object targetObj = _cls.newInstance();
			            result = method.invoke(targetObj, args);
					}
				}
			} else {
				result = method.invoke(_target, args);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}
}
