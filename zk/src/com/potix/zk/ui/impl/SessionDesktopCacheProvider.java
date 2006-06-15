/* SessionDesktopCacheProvider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Apr 18 11:52:51     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.sys.DesktopCache;
import com.potix.zk.ui.sys.DesktopCacheProvider;

/**
 * A implementation of {@link DesktopCacheProvider} that stores all desktops
 * from the same session in one desktop cache.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @see GlobalDesktopCacheProvider
 */
public class SessionDesktopCacheProvider implements DesktopCacheProvider {
	private static final String ATTR_CACHE = "javax.potix.zk.desktop-cache";
	private WebApp _wapp;

	//-- DesktopCacheProvider --//
	public DesktopCache getCache(Session sess) {
		DesktopCache dc = (DesktopCache)sess.getAttribute(ATTR_CACHE);
		if (dc == null) {
			synchronized (this) {
				dc = (DesktopCache)sess.getAttribute(ATTR_CACHE);
				if (dc == null) {
					dc = new SimpleDesktopCache(_wapp.getConfiguration());
					sess.setAttribute(ATTR_CACHE, dc);
				}
			}
		}
		return dc;
	}
	public void sessionDestroyed(Session sess) {
		DesktopCache dc = (DesktopCache)sess.getAttribute(ATTR_CACHE);
		if (dc != null) {
			sess.removeAttribute(ATTR_CACHE);
			dc.stop();
		}
	}

	public void sessionWillPassivate(Session sess) {
	}
	public void sessionDidActivate(Session sess) {
		//TODO
	}

	public void start(WebApp wapp) {
		_wapp = wapp;
	}
	public void stop(WebApp wapp) {
		_wapp = null;
	}
}
