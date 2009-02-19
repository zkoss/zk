/* XelContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 12:23:25     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;


/**
 * Context information for XEL evalution.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface XelContext {
	/** Returns the variable resolver, or null if not available.
	 */
	public VariableResolver getVariableResolver();
	/** Returns the function mapper, or null if not available.
	 */
	public FunctionMapper getFunctionMapper();
}
