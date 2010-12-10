/* WorkingThread2.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  7 18:42:47     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import java.util.List;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zul.Label;

/**
 * Another implementation that don't use Executions.wait and notify.
 *
 * @author tomyeh
 */
public class WorkingThread2 extends Thread {
	private static int _cnt;

	private final Desktop _desktop;
	private final List _result;

	public WorkingThread2(Desktop desktop, List result) {
		_desktop = desktop;
		_result = result;
	}
	public void run() {
		_result.add(new Label("Execute "+ ++_cnt));
	}
}
