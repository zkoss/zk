/* ExecutionMonitor.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul  5 17:22:39 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zkdemo.test;

import java.util.Date;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;

/**
 * An implementation of ExecutionMonitor.
 * <p>How to use:
 * <ol>
 * <li>Specify a listener called org.zkoss.zkdemo.test.ExecutionMonitor
 * in WEB-INF/zk.xml (refer zk-test.xml)</li>
 * <li>Refer to console to see the activation/deactivation of executions.</li>
 * @author tomyeh
 * @since 5.5.0
 */
public class ExecutionMonitor implements
org.zkoss.zk.ui.util.ExecutionMonitor {
	public void executionActivate(Execution exec, Desktop desktop) {
		System.out.println(new Date()+"+executionActivate:" + exec + ":" + desktop);
	}
	public void executionWait(Execution exec, Desktop desktop) {
		System.out.println(new Date()+".executionWait:" + exec + ":" + desktop);
	}
	public void executionDeactivate(Execution exec, Desktop desktop) {
		System.out.println(new Date()+"-executionDeactivate:" + exec + ":" + desktop);
	}
	public void executionAbort(Execution exec, Desktop desktop, Throwable t) {
		System.out.println(new Date()+"!executionAbort:" + exec + ":" + desktop + ":" + t);
	}

	public void desktopDestroy(Desktop desktop) {
		System.out.println(new Date()+"#desktopDestroy:" + desktop);
	}

	public void eventStart(Event event) {
		System.out.println(new Date()+"+eventStart:" + event);
	}
	public void eventComplete(Event event) {
		System.out.println(new Date()+"-eventComplete:" + event);
	}
	public void eventSuspend(Event event) {
		System.out.println(new Date()+"[eventSuspend:" + event);
	}
	public void eventResume(Event event) {
		System.out.println(new Date()+"]eventResume:" + event);
	}
}
