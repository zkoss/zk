/* LocaleProvider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 13 15:43:35     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.Locale;
import org.zkoss.zk.ui.Session;

/**
 * Provides the locale for the specified session.
 * Once specified (in {@link org.zkoss.zk.ui.util.Configuration}), an instance of the specified class
 * is created and then {@link #getLocale} is called, is called each time
 * a request from the client is received.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface LocaleProvider {
	/** Returns the locale of the specified session, or null if the default
	 * shall be used.
	 *
	 * <p>The default is determined by browser's preference, if any.
	 */
	public Locale getLocale(Session sess);
}
