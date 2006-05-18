/* RequestInfo.java

{{IS_NOTE
	$Id: RequestInfo.java,v 1.3 2006/04/24 06:32:52 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Apr 19 14:02:14     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import com.potix.util.resource.Locator;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Session;

/**
 * The request information used with {@link com.potix.zk.ui.sys.UiFactory}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/04/24 06:32:52 $
 */
public interface RequestInfo {
	/** Returns the Web application; never null.
	 */
	public WebApp getWebApp();
	/** Returns the session; never null.
	 */
	public Session getSession();
	/** Returns the desktop, or null if {@link com.potix.zk.ui.sys.UiFactory#newDesktop}
	 * is called.
	 */
	public Desktop getDesktop();
	/** Returns the native request. For HTTP, it is
	 * javax.servlet.http.HttpServletRequest. For portlets, it is
	 * javax.portlet.RenderRequest
	 */
	public Object getNativeRequest();

	/** Returns the locator used to retrieve resources, such as
	 * taglib and zscript files.
	 *
	 * <p>The default locator is based on the servlet context. If
	 * you want a different locator, use {@link #setLocator} to change
	 * the default value.
	 */
	public Locator getLocator();
	/** Sets the locator used to retrieve resources, such as
	 * taglib and zscript files.
	 */
	public void setLocator(Locator locator);
}
