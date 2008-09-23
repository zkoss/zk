/* GlobalDesktopCacheProvider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Apr 18 14:12:16     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.io.Serializable;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.DesktopCacheProvider;
import org.zkoss.zk.ui.sys.DesktopCache;

/**
 * A implementation of {@link DesktopCacheProvider} that stores all
 * desktops from the same Web application into one desktop cache.
 *
 * <p>In other words, it ignores the session, and it depends only on
 * {@link WebApp}.
 * 
 * @author tomyeh
 * @see SessionDesktopCacheProvider
 */
public class GlobalDesktopCacheProvider implements DesktopCacheProvider,
Serializable {
	private static final String ATTR_CACHE = "javax.zkoss.zk.desktop-cache";
    private static final long serialVersionUID = 20060622L;

	//-- DesktopCacheProvider --//
	public DesktopCache getDesktopCache(Session sess) {
		final WebApp wapp = sess.getWebApp();
		DesktopCache dc = (DesktopCache)wapp.getAttribute(ATTR_CACHE);
		if (dc == null) {
			synchronized (this) {
				dc = (DesktopCache)wapp.getAttribute(ATTR_CACHE);
				if (dc == null) {
					dc = new SimpleDesktopCache(
						sess.getWebApp().getConfiguration());
					wapp.setAttribute(ATTR_CACHE, dc);
				}
			}
		}
		return dc;
	}
	public void sessionDestroyed(Session sess) {
		//ignore it
	}

	/** Invokes {@link #getDesktopCache}'s {@link DesktopCache#sessionWillPassivate}.
	 */
	public void sessionWillPassivate(Session sess) {
		DesktopCache dc = (DesktopCache)sess.getAttribute(ATTR_CACHE);
		if (dc != null) dc.sessionWillPassivate(sess);
	}
	/** Invokes {@link #getDesktopCache}'s {@link DesktopCache#sessionDidActivate}.
	 */
	public void sessionDidActivate(Session sess) {
		DesktopCache dc = (DesktopCache)sess.getAttribute(ATTR_CACHE);
		if (dc != null) dc.sessionDidActivate(sess);
	}

	public void start(WebApp wapp) {
		//ignore it
	}
	public void stop(WebApp wapp) {
		DesktopCache dc = (DesktopCache)wapp.getAttribute(ATTR_CACHE);
		if (dc != null) {
			wapp.removeAttribute(ATTR_CACHE);
			dc.stop();
		}
	}
}
