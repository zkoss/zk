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

import java.util.Collection;

/**
 * Used to customize the way to map between the XEL function names
 * and the {@link Function} methods.
 *
 * @author tomyeh
 * @since 2.4.2
 */
public interface FunctionMapper {
	/** Resolves a function {@link Function} with the specified name and
	 * prefix.
	 *
	 * @param prefix the prefix of the function, or "" if no prefix
	 * @param name the name of the function to resolve
	 */
	public Function resolveFunction(String prefix, String name)
	throws XelException;

	/** Returns a readonly collection of the logic names of the class
	 * (never null).
	 *
	 * <p>It is used only with ZK 3.0 and later.
	 * It is not used in ZK 2.4.x.
	 */
	public Collection getClassNames();
	/** Resolves a class with the specified logic name,
	 * or null if not found.
	 *
	 * <p>It is used only with ZK 3.0 and later.
	 * It is not used in ZK 2.4.x.
	 */
	public Class resolveClass(String name) throws XelException;
}
