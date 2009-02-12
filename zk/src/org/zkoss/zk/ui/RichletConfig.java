/* RichletConfig.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct  5 13:13:16     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Iterator;

/**
 * A richlet configuration object used by a richlet container to pass information
 * to a richlet during initialization.
 *
 * @author tomyeh
 */
public interface RichletConfig {
	/** Returns the web application that the richlet belongs to.
	 */
	public WebApp getWebApp();

	/** Returns a String containing the value of the named initialization
	 * parameter, or null if the parameter does not exist.
	 */
	public String getInitParameter(String name);
	/** Returns the names of the richlet's initialization parameters as
	 * an Iterator of String objects, or an empty Enumeration
	 * if the richlet has no initialization parameters.
	 */
	public Iterator getInitParameterNames();
}
