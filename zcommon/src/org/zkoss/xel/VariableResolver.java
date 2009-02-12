/* VariableResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 10:17:36     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

/**
 * Used to customize the way an {@link Expression} resolves variable
 * references at evaluation time.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface VariableResolver {
	/** Resolves the specified variable.
	 *
	 * @param name the name of the variable to resolve
	 */
	public Object resolveVariable(String name)
	throws XelException;
}
