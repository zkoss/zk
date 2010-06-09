/* XelContext.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 12:23:25     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

import java.util.Map;

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

	/** Returns the value of the specified attribute, or null if not available.
	 * @param name the name of the attribute to retrieve
	 * @return the value
	 * @since 5.0.0
	 */
	public Object getAttribute(String name);
	/** Sets the value of the specified attribute
	 * @param name the name of the attribute to set
	 * @param value the value of the attribute to set
	 * @return the previous value
	 * @since 5.0.0
	 */
	public Object setAttribute(String name, Object value);
	/** Returns if the attribute is available.
	 * <p>Notice that <code>null</code> is a valid value, so you can
	 * tell if an attribute is assoicated by examining the return value
	 * of {@link #getAttribute}.
	 * @param name the name of the attribute to test
	 * @since 5.0.0
	 */
	public boolean hasAttribute(String name);
	/** Removes the attribute.
	 * @param name the name of the attribute to remove.
	 * @return the previous value associated with the attribute, if any,
	 * @since 5.0.0
	 */
	public Object removeAttribute(String name);
	/** Returns all attributes
	 * @since 5.0.0
	 */
	public Map getAttributes();
}
