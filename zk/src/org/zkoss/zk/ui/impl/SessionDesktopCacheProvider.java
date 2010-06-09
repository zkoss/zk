/* SessionDesktopCacheProvider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Apr 18 11:52:51     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.DesktopCacheProvider;

/**
 * A implementation of {@link DesktopCacheProvider} that stores all desktops
 * from the same session in one desktop cache.
 *
 * <p>Implementation Note: we don't store the desktop cache in the session's
 * attribute (rather, we use {@link SessionCtrl#setDesktopCache}. Reason:
 * the desktop cache is serialized only if the session is serializable.
 *
 * @author tomyeh
 * @see GlobalDesktopCacheProvider
 */
public class SessionDesktopCacheProvider implements DesktopCacheProvider {
	private WebApp _wapp;

	//-- DesktopCacheProvider --//
	public DesktopCache getDesktopCache(Session sess) {
		final SessionCtrl sessCtrl = (SessionCtrl)sess;
		DesktopCache dc = sessCtrl.getDesktopCache();
		if (dc == null) {
			synchronized (this) {
				dc = sessCtrl.getDesktopCache();
				if (dc == null) {
					dc = new SimpleDesktopCache(_wapp.getConfiguration());
					sessCtrl.setDesktopCache(dc);
				}
			}
		}
		return dc;
	}
	public void sessionDestroyed(Session sess) {
		final SessionCtrl sessCtrl = (SessionCtrl)sess;
		final DesktopCache dc = sessCtrl.getDesktopCache();
		if (dc != null) {
			sessCtrl.setDesktopCache(null);
			dc.stop();
		}
	}

	/** Invokes {@link #getDesktopCache}'s {@link DesktopCache#sessionWillPassivate}.
	 */
	public void sessionWillPassivate(Session sess) {
		final DesktopCache dc = ((SessionCtrl)sess).getDesktopCache();
		if (dc != null) dc.sessionWillPassivate(sess);
	}
	/** Invokes {@link #getDesktopCache}'s {@link DesktopCache#sessionDidActivate}.
	 */
	public void sessionDidActivate(Session sess) {
		final DesktopCache dc = ((SessionCtrl)sess).getDesktopCache();
		if (dc != null) dc.sessionDidActivate(sess);
	}

	public void start(WebApp wapp) {
		_wapp = wapp;
	}
	public void stop(WebApp wapp) {
		_wapp = null;
	}
}
