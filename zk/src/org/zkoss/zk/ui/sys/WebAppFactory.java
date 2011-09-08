/* WebAppFactory.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 12:02:06 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.Configuration;

/**
 * The factory used to instantiate the instance of {@link WebApp}.
 * To instantiate UI objects (such as desktop, page, component and session),
 * please implement {@link UiFactory} instead.
 * @author tomyeh
 * @since 5.5.0
 * @see UiFactory
 */
public interface WebAppFactory {
	/** Instantiates the instance of {@link WebApp}.
	 * @param ctx the application context.
	 * For Servlet, it is javax.servlet.ServletContext.
	 */
	public WebApp newWebApp(Object ctx, Configuration config);
}
