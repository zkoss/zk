/* VariableResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 12:09:52     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.scripting;

/**
 * The name resolver used with {@link org.zkoss.zk.ui.Page#getVariable}.
 *
 * @author tomyeh
 */
public interface VariableResolver {
	/** Returns the value of the specified variable, or null if not found.
	 */
	public Object getVariable(String name);
}
