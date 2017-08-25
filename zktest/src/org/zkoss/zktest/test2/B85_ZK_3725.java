/* B85_ZK_3725.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 16 16:25:57 CST 2017, Created by wenninghsu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Composer;

/**
 * 
 * @author wenninghsu
 */
public class B85_ZK_3725 implements Composer<Component> {

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		desktop.enableServerPush(true);

		Thread t1 = asyncTask(desktop, "T1", 0, 2000);
		final Thread t2 = asyncTask(desktop, "T2", 1000, 2000);
		Thread t3 = asyncTask(desktop, "T3", 4000, 2000);
		Thread t4 = asyncTask(desktop, "T4", 5000, 2000);

		t1.start();
		t2.start();
		t3.start();
		t4.start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1500);
					System.out.println("interrupt thread: T2");
					t2.interrupt();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private Thread asyncTask(final Desktop desktop, final String threadName, final long initialDelay, final int blockingDelay) {
		final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(initialDelay);

					System.out.println(threadName + " waiting to activate ...");
					Executions.activate(desktop);

					System.out.println(threadName + " begin");
					Thread.sleep(blockingDelay);
					Clients.log(threadName + " TaskResult for UI");
					System.out.println(threadName + " done");

				} catch (InterruptedException e) {
					System.err.println(threadName + " interupted waiting for " + e);
					//throw new RuntimeException(threadName, e);
				} finally {
					Executions.deactivate(desktop);
					System.out.println(threadName + " deactivated");
				}
			}
		});
		thread.setName(threadName);
		return thread;
	}
}
