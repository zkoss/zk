/* F86_ZK_4256Composer.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 15 16:11:06 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

/**
 * @author rudyhuang
 */
public class F86_ZK_4256Composer extends SelectorComposer<Component> {
	private Timer timer;

	@Wire
	private Label time;

	@Listen("onClick = #start")
	public void startWorker() {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			Messagebox.show("Already started");
		} else {
			desktop.enableServerPush(true);
			if (timer != null) timer.cancel();
			timer = new Timer();
			timer.schedule(createUpdateTask(), 0, 1000);
		}
	}

	private TimerTask createUpdateTask() {
		return new TimerTask() {
			@Override
			public void run() {
				updateTime();
			}
		};
	}

	private void updateTime() {
		try {
			Desktop desktop = time.getDesktop();
			if (desktop == null) {
				timer.cancel();
				return;
			}

			Executions.activate(desktop);
			try {
				time.setValue(new Date().toString());
			} finally {
				Executions.deactivate(desktop);
			}
		} catch (DesktopUnavailableException ex) {
			System.out.println("Desktop currently unavailable");
			timer.cancel();
		} catch (InterruptedException e) {
			System.out.println("The server push thread interrupted");
			timer.cancel();
		}
	}

	@Listen("onClick = #stop")
	public void stopWorker() {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		if (desktop.isServerPushEnabled()) {
			desktop.enableServerPush(false);
		} else {
			Messagebox.show("Already stopped");
		}
		timer.cancel();
	}
}
