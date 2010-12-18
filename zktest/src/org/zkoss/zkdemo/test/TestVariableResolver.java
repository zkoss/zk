/* TestVariableResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Mar 29 18:13:09     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import java.util.List;
import java.util.LinkedList;

import org.zkoss.xel.VariableResolver;

/**
 * An implementation of the variable resolver to test whether it works.
 * <p>Test: http://localhost/zkdemo/test/vresolver.zul
 *
 * <p>It resolves friends to a list of String instances.
 *
 * @author tomyeh
 */
public class TestVariableResolver implements VariableResolver {
	private final List _friends = new LinkedList();

	public TestVariableResolver() {
		String[] ss = {"Tom", "Jane", "John", "Mary"};
		for (int j = 0; j < ss.length; ++j)
			_friends.add(ss[j]);
	}

	//VariableResolver//
	public Object resolveVariable(String name) {
		return "friends".equals(name) ? _friends: null;
	}
}
