/* Scope.java

	Purpose:
		
	Description:
		
	History:
		Thu Feb 19 09:45:14     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.ext;

import java.util.Map;

/**
 * Represents a scope of attributes.
 * @author tomyeh
 * @since 3.6.0
 */
public interface Scope {
	/** Returns all custom attributes associated with this object.
	 */
	public Map getAttributes();
	/** Returns the custom attribute associated with this object.
	 */
	public Object getAttribute(String name);
}
