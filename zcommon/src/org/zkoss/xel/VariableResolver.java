/* VariableResolver.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 10:17:36     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

/**
 * Used to customize the way an {@link Expression} resolves variable
 * references at evaluation time.
 *
 * <p>Since 5.0.0, you can implement another interface called {@link VariableResolverX}
 * to have more control about resolving the variables.
 *
 * @author tomyeh
 * @since 3.0.0
 * @see VariableResolverX
 */
public interface VariableResolver {
	/** Resolves the specified variable.
	 *
	 * @param name the name of the variable to resolve
	 */
	public Object resolveVariable(String name)
	throws XelException;
}
