/* B96_ZK_4809Composer.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 18 12:47:43 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;

/**
 * @author rudyhuang
 */
public class B96_ZK_4809Composer extends SelectorComposer<Component> {
	private final AtomicBoolean _running = new AtomicBoolean(false);

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Executions.getCurrent().getDesktop().enableServerPush(true);
	}

	@Listen("onClick=#start")
	public void startLongOp() {
		_running.set(true);
		logThread("Start");

		final Desktop desktop = Executions.getCurrent().getDesktop();
		CompletableFuture.runAsync(() -> {
			logThread("started");
			while (_running.get()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			logThread("stopped");
			try {
				Executions.activate(desktop);
				logThread("activated");
				// set an attribute to the current Request object
				Executions.getCurrent().setAttribute("test", "1234");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				logThread("deactivate");
				//comment this out, and comment in the next line for another Exception (not shown in the UI)
				Events.postEvent("onCustom", this.getSelf(), null);
				// removing the request attribute here, causes a different kind of exception
//				String test = (String) Executions.getCurrent().removeAttribute("test");
				Executions.deactivate(desktop);
			}
		});
	}

	@Listen("onClick=#stop")
	public void stopLongOp() {
		_running.set(false);
	}

	@Listen("onCustom=#main")
	public void handleCustomEvent() {
		logThread("onCustom");
		// ERROR happens here when removing the request attribute
		String test = (String) Executions.getCurrent().removeAttribute("test");
		logThread("removed attribute 'test'=" + test);
	}

	private void logThread(String msg) {
		Execution exec = Executions.getCurrent();
		Object req = null;
		if (exec != null) {
			req = exec.getNativeRequest();
		}
		System.out.println(String.format("[%s] %s %s %s",
				Thread.currentThread().getName(), msg, exec, req));
	}
}
