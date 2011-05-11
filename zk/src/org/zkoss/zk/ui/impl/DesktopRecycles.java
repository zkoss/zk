/* DesktopRecycles.java

	Purpose:
		
	Description:
		
	History:
		Thu May  5 09:25:05 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.impl;

import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.DesktopRecycle;

/**
 * Utilties to handle {@link DesktopRecycle}.
 * @author tomyeh
 * @since 5.0.7
 */
public class DesktopRecycles {
	private static final Log log = Log.lookup(DesktopRecycles.class);

	/** Called to remove the desktop.
	 * If {@link DesktopRecycle} is configured, {@link DesktopRecycle#beforeRemove}
	 * will be called first to see if it shall be recycled.
	 * @return whether the desktop is removed. Returns false if the desktop
	 * is recycled.
	 */
	public static boolean removeDesktop(Execution exec) {
		final Desktop dt = exec.getDesktop();
		final WebApp wapp = dt.getWebApp();
		final DesktopRecycle dtrc = wapp.getConfiguration().getDesktopRecycle();
		if (dtrc != null) {
			try {
				if (dtrc.beforeRemove(exec, dt, 0)) {
					((DesktopCtrl)dt).recycle();
					return false; //recycled
				}
			} catch (Throwable ex) {
				log.error(ex);
			}
		}
		((WebAppCtrl)wapp).getDesktopCache(dt.getSession()).removeDesktop(dt);
		return true;
	}
}
