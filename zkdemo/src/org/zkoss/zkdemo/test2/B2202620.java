/* B2202620.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 28 10:33:43     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

/**
 * 
 * @author tomyeh
 */
public class B2202620 {
	public static void start(Component info) throws InterruptedException {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			Messagebox.show("Already started");
		} else {
			desktop.removeAttribute("sp.ceased");
			desktop.enableServerPush(true);
			new WorkingThread(info).start();
		}
	}
	public static void stop() throws InterruptedException {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			desktop.enableServerPush(false);
			desktop.setAttribute("sp.ceased", Boolean.TRUE);
		} else {
			Messagebox.show("Already stopped");
		}
	}

	private static class WorkingThread extends Thread {
		private final Desktop _desktop;
		private final Component _info;
		private WorkingThread(Component info) {
			_desktop = info.getDesktop();
			_info = info;
		}
		public void run() {
			try {
				while (_desktop.getAttribute("sp.ceased") == null) {
					Executions.activate(_desktop);
					try {
						_info.appendChild(new Label("Comet received"));
						_desktop.enableServerPush(false);
					} finally {
						Executions.deactivate(_desktop);
					}
					Threads.sleep(500);
				}
				System.out.println("The server push thread ceased");
			} catch (InterruptedException ex) {
				System.out.println("The server push thread interrupted");
			}
		}
	}
}
