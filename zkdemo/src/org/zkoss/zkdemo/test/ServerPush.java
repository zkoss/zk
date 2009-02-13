/* ServerPushWindow.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug  6 12:37:13     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import org.zkoss.lang.Threads;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zul.*;

/**
 * Used to test the server-push feature.
 *
 * @author tomyeh
 */
public class ServerPush {
	private static final Log log = Log.lookup(ServerPush.class);
	private static boolean _ceased;

	public static void start(Label info) throws InterruptedException {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			Messagebox.show("Already started");
		} else {
			_ceased = false;
			desktop.enableServerPush(true);
			new WorkingThread(info).start();
		}
	}
	public static void stop() throws InterruptedException {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			Executions.getCurrent().getDesktop().enableServerPush(false);
			_ceased = true;
		} else {
			Messagebox.show("Already stopped");
		}
	}

	private static class WorkingThread extends Thread {
		private final Desktop _desktop;
		private final Label _info;
		private int _cnt;
		private WorkingThread(Label info) {
			_desktop = info.getDesktop();
			_info = info;
		}
		public void run() {
			try {
				while (!_ceased) {
					Executions.activate(_desktop);
					try {
						_info.setValue(Integer.toString(++_cnt));
					} catch (RuntimeException ex) {
						log.error(ex);
						throw ex;
					} catch (Error ex) {
						log.error(ex);
						throw ex;
					} finally {
						Executions.deactivate(_desktop);
					}
					Threads.sleep(500); //Update each two seconds
				}
				log.info("The server push thread ceased");
			} catch (InterruptedException ex) {
				log.info("The server push thread interrupted", ex);
			}
		}
	}
}
