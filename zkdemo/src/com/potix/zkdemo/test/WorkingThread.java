/* WorkingThread.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  7 16:50:27     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zkdemo.test;

import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Executions;
import com.potix.zul.html.Label;

/**
 * Illustrates how an event handler invokes a working thread
 * to execute a long operation.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
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
			return worker.getLabel();
		}
	}
	public WorkingThread(Desktop desktop) {
		_desktop = desktop;
	}
	/** Return the generated label.
	 */
	public Label getLabel() {
		return _label;
	}
	public void run() {
		_label = new Label("Execute "+ ++_cnt);
		synchronized (_mutex) {
			Executions.notify(_desktop, _mutex);
		}
	}
}
