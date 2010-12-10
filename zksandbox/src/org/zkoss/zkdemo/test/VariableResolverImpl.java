/* VariableResolverImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec  6 18:25:36     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

/**
 * Test of variable resolver.
 *
 * @author tomyeh
 */
public class VariableResolverImpl implements org.zkoss.xel.VariableResolver {
	public VariableResolverImpl() {
		System.out.println("VariableResolverImpl()");
	}
	public VariableResolverImpl(String s) {
		System.out.println("VariableResolverImpl(String)");
	}
	public Object resolveVariable(String name) {
		return null;
	}
}
