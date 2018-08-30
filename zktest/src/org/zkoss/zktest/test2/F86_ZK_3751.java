package org.zkoss.zktest.test2;

import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import java.util.Date;


public class F86_ZK_3751 {

	public static void start(Label info) {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			Messagebox.show("Already started");
			new F86_ZK_3751.WorkingThread(info).start();
		} else {
			desktop.enableServerPush(true);
			new F86_ZK_3751.WorkingThread(info).start();
		}
	}

	public static void stop() {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			desktop.enableServerPush(false);
		} else {
			Messagebox.show("Already stopped");
		}
	}

	private static class WorkingThread extends Thread {

		private final Desktop _desktop;

		private final Label _info;

		private WorkingThread(Label info) {
			_desktop = info.getDesktop();
			_info = info;
		}

		public void run() {
			try {
				while (true) {
					if (_info.getDesktop() == null || !_desktop.isServerPushEnabled()) {
						_desktop.enableServerPush(false);
						return;
					}
					Executions.activate(_desktop);
					try {
						_info.setValue(new Date().toString());
					} finally {
						Executions.deactivate(_desktop);
					}
					Threads.sleep(1000);
				}
			} catch (DesktopUnavailableException ex) {
				System.out.println("The server push thread interrupted");
			} catch (InterruptedException e) {
				System.out.println("The server push thread interrupted");
			}
		}

	}

}
