/* ServerPushWindow.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug  6 12:37:13     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zul.Label;

/**
 * Used to test the server-push feature.
 *
 * @author tomyeh
 */
public class ServerPush {
	private static final Log log = Log.lookup(ServerPush.class);

	public static void start(Label info) {
		final Desktop desktop = info.getDesktop();
		desktop.enableServerPush(true);
		new WorkingThread(desktop).start();
	}

	private static class WorkingThread extends Thread {
		private final Desktop _desktop;
		private WorkingThread(Desktop desktop) {
			_desktop = desktop;
		}
		public void run() {
			
			Executions.activate(
		}
	}
}
