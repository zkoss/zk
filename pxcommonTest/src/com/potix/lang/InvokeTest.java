/* InvokeTest.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommonTest/src/com/potix/lang/InvokeTest.java,v 1.2 2006/02/27 03:42:09 tomyeh Exp $
	Purpose: Test the performance of different invocations.
	Description:
	History:
	 2001/4/12, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.lang;

import junit.framework.*;

import java.util.*;
import java.lang.reflect.*;

public class InvokeTest extends TestCase {
	public static final int LOOP = 1000000;
	private A a = new A();
	private final Map map = new HashMap();
	public InvokeTest(String name) {
		super(name);
		map.put(a, "a1");
		map.put(new A(), "a2");
		map.put(new A(), "a3");
	}
	public static Test suite() {
		return new TestSuite(InvokeTest.class);
	}
	protected void setUp() {
	}
	protected void tearDown() {
	}

	public void testDirectSpeed() throws Exception {
		//Result: 0.01s
		for (int j = LOOP; --j >= 0;)
			a.f();
	}
	public void testInterfaceSpeed() throws Exception {
		//Result: 0.04s
		I i = a;
		for (int j = LOOP; --j >= 0;)
			i.f();
	}
	public void testReflectSpeed() throws Exception {
		//Result: 0.591s
		//Note: 20040816: Tom Yeh
		//1) whether getMethod doesn't affect performance much (might due to big LOOP)
		//2) whether Method.modifier is public improve 7.5x
		//Method m = A.class.getMethod("f", null); //slow (4.4s)
		Method m = I.class.getMethod("f", null); //faster (0.591s)
		for (int j = LOOP; --j >= 0;)
			m.invoke(a, null);
	}
	public void testProxySpeed() throws Exception {
		//Result: 0.631s
		final I i = (I)Proxy.newProxyInstance(A.class.getClassLoader(),
			new Class [] {I.class}, new Invoker(a));
		for (int j = LOOP; --j >= 0;)
			i.f();
	}
	public void testHashSpeed() throws Exception {
		//Result: 0.181s
		for (int j = LOOP; --j >= 0;)
			map.get(a);
	}
	public void testIdentitySpeed() throws Exception {
		//Result: 0.35s
		for (int j = LOOP; --j >= 0;)
			System.identityHashCode(a);
	}
	public static interface I {
		public void f();
	}
	static class A implements I {
		A() {
		}
		public void f() {
		}
	}
	static class Invoker implements InvocationHandler {
		I _i;
		public Invoker(I i) {
			_i = i;
		}
		public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {
			return method.invoke(_i, args);
		}
	}
}
