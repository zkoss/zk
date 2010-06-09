/* B2227929Resolver.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  6 18:58:05     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zkdemo.test2;

/**
 * Used to test B30-2227929-inc.zul.
 * @author tomyeh
 */
public class B2227929Resolver implements org.zkoss.xel.VariableResolver {
	public Object resolveVariable(String name) {
		return "v2227929".equals(name) ? "2227929": null;
	}
}
