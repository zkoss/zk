/* FilterConfig.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 23 11:06:12     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

/**
 * A filter configuration object used by a servlet container to pass
 * information to a filter during initialization.
 * @author tomyeh
 * @since 3.5.1
 */
public interface FilterConfig {
	/** Returns the Extendlet context.
	 */
	public ExtendletContext getExtendletContext();
}
