/* WorkingThread.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  7 16:50:27     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Label;

/**
 * Illustrates how an event handler invokes a working thread
 * to execute a long operation.
 *
 * @author tomyeh
 */
public class WorkingThread extends Thread {
	private static int _cnt;

	private Desktop _desktop;
	private Label _label;
	private final Object _mutex = new Integer(0);

	/** Called by thread.zul to create a label asynchronously.
	 * To create a label, it start a thread, and wait for its completion.
	 */
	public static final Label asyncCreate(Desktop desktop)
	throws InterruptedException {
		final WorkingThread worker = new WorkingThread(desktop);
		synchronized (worker._mutex) {
			worker.start();
			Executions.wait(worker._mutex);
			return worker._label;
		}
	}

	public WorkingThread(Desktop desktop) {
		_desktop = desktop;
	}
	public void run() {
		_label = new Label("Execute "+ ++_cnt);
		synchronized (_mutex) {
			Executions.notify(_desktop, _mutex);
		}
	}
}
