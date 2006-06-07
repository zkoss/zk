/* WorkingThread2.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  7 18:42:47     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zkdemo.test;

import java.util.List;

import com.potix.zk.ui.Desktop;
import com.potix.zul.html.Label;

/**
 * Another implementation that don't use Executions.wait and notify.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
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
