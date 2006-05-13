/* WebAppCtrl.java

{{IS_NOTE
	$Id: WebAppCtrl.java,v 1.2 2006/04/19 05:10:40 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Apr 18 11:07:30     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import com.potix.zk.ui.Session;
import com.potix.zk.ui.sys.DesktopCache;
import com.potix.zk.ui.sys.DesktopCacheProvider;
import com.potix.zk.ui.sys.UiFactory;

/**
 * Additional interface of {@link com.potix.zk.ui.WebApp} for implementation.
 * <p>Note: applications shall never access this interface.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/04/19 05:10:40 $
 */
public interface WebAppCtrl {
	/** Returns the UI engine for this session.
	 */
	public UiEngine getUiEngine();
	/** Returns the desktop cache.
	 * A shortcut of {@link #getDesktopCacheProvider}'s
	 * {@link DesktopCacheProvider#getCache}.
	 */
	public DesktopCache getDesktopCache(Session sess);
	/** Returns the desktop cache provider.
	 */
	public DesktopCacheProvider getDesktopCacheProvider();
	/** Returns the UI factory.
	 */
	public UiFactory getUiFactory();
}
