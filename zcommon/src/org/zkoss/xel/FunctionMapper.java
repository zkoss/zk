/* FunctionMapper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 10:17:55     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

/**
 * Used to customize the way to map between the XEL function names
 * and the {@link Function} methods.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface FunctionMapper {
	/** Resolves the specified name and prefix into a function
	 * {@link Function}.
	 *
	 * @param prefix the prefix of the function, or "" if no prefix
	 * @param name the name of the function to resolve
	 */
	public Function resolveFunction(String prefix, String name)
	throws XelException;
}
