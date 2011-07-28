/* FunctionMapper.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 10:17:55     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

/**
 * Used to customize the way to map between the XEL function names
 * and the {@link Function} methods.
 *
 * @author tomyeh
 * @see FunctionMapperExt
 * @since 3.0.0
 */
public interface FunctionMapper {
	/** Resolves a function {@link Function} with the specified name and
	 * prefix.
	 *
	 * <p>Note: not all EL evaluator support {@link #resolveFunction}.
	 * Currently only JSP 2.0/2.1 EL-based expression factories
	 * support this method.
	 * You can check {@link ExpressionFactory#isSupported} for this
	 * support.
	 *
	 * @param prefix the prefix of the function, or "" if no prefix
	 * @param name the name of the function to resolve
	 */
	public Function resolveFunction(String prefix, String name)
	throws XelException;
}
