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

import org.zkoss.lang.Threads;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zul.Label;

/**
 * Used to test the server-push feature.
 *
 * @author tomyeh
 */
public class ServerPush {
	private static final Log log = Log.lookup(ServerPush.class);
	private static boolean _ceased;

	public static void start(Label info) {
		Executions.getCurrent().getDesktop().enableServerPush(true);
		new WorkingThread(info).start();
	}
	public static void stop() {
		Executions.getCurrent().getDesktop().enableServerPush(false);
		_ceased = true;
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
			while (!_ceased) {
				Threads.pause(2000); //Update each two seconds
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
			}
			log.info("The server push thread ceased");
		}
	}
}
