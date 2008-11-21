/* CometServerPush.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 20, 2008 11:31:54 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkdemo.userguide;

import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Separator;

/**
 * @author jumperchen
 * 
 */
public class CometServerPush {
	private static String[] news = new String[] {
		"Potix (ZK) forms partnership with Centigrade",
		"ZK Jet 0.8.0 version is released",
		"Style Guide for ZK 3.5 released ",
		"ZK STudio 0.9.0 released",
		"Potix Named as \"Red Herring 100 Awards 2008\" Finalist",
		"ZK With Spring JPA And A Model-View-Controller Pattern",
		"Use Theme Tool to Customize your Own Theme"
	};
	public static void start(Component info) throws InterruptedException {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			Messagebox.show("Already started");
		} else {
			desktop.enableServerPush(true);
			new WorkingThread(info).start();
		}
	}

	public static void stop() throws InterruptedException {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			desktop.enableServerPush(false);
		} else {
			Messagebox.show("Already stopped");
		}
	}

	private static class WorkingThread extends Thread {
		private final Desktop _desktop;

		private int count = 0;
		
		private final Component _info;

		private WorkingThread(Component info) {
			_desktop = info.getDesktop();
			_info = info;
		}

		public void run() {
			try {
				while (true) {
					Executions.activate(_desktop);
					try {
						Label label = new Label();
						synchronized(this) {
							label.setValue(news[count++]);
							if (count == 7) count =0;
						}
						label.setSclass("comet-news");
						if ( _info.getFirstChild() == null) {
							 _info.appendChild(label);
						} else {
							_info.insertBefore(label, _info.getFirstChild());
						}
						 _info.insertBefore(new Separator(), _info.getFirstChild());
						if (_info.getChildren().size() > 12) {
							_info.getLastChild().detach();
							_info.getLastChild().detach();
						}
					} finally {
						Executions.deactivate(_desktop);
					}
					Threads.sleep(1000);
				}
			} catch (InterruptedException ex) {
				System.out.println("The server push thread interrupted");
			}
		}
	}
}
