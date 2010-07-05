/* ExecutionMonitor.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 30 14:15:16 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;

/**
 * An application-level listener to know when an execution is activated,
 * deactivated, or waiting for activation.
 *
 * <p>Notice that the same listener is used for the whole application.
 * Make sure it is thread-safe and the performance is good.
 * In additions, don't throw any exception, and don't hold a reference to execution.
 * <p>If you hold references to desktops, remember to clean them up
 * when {@link #desktopDestroy} is called.
 *
 * @author tomyeh
 * @since 5.5.0
 * @see PerformanceMeter
 */
public interface ExecutionMonitor {
	/** Called when an execution is activated.
	 * There is only one execution per desktop is allowed to activate at
	 * the same time.
	 */
	public void executionActivate(Execution exec, Desktop desktop);
	/** Called when an execution fails to activate and then being waiting
	 * for activation.
	 * <p>Since 5.0, the AU requests are blocked at the client if ZK is busy
	 * for processing an AU request. Thus, this method is rarely called.
	 */
	public void executionWait(Execution exec, Desktop desktop);
	/** Called when an activated execution completes and is deactivated.
	 */
	public void executionDeactivate(Execution exec, Desktop desktop);
	/** Called when the activation of the given execution is aborted.
	 *
	 * @param t the exception causing the abort, or null if it is aborted
	 * normally.
	 */
	public void executionAbort(Execution exec, Desktop desktop, Throwable t);

	/** Called when a desktop is destroyed.
	 * This callback is useful to clean up the memory,
	 * if you have references to desktops.
	 */
	public void desktopDestroy(Desktop desktop);

	/** Called when the processing of the given event starts.
	 * The execution can be found by {@link org.zkoss.zk.ui.Executions#getCurrent}.
	 */
	public void eventStart(Event event);
	/** Called when the processing of the given event completes.
	 * The execution can be found by {@link org.zkoss.zk.ui.Executions#getCurrent}.
	 */
	public void eventComplete(Event event);
	/** Called when the processing of the given event suspends.
	* The execution can be found by {@link org.zkoss.zk.ui.Executions#getCurrent}.
	 */
	public void eventSuspend(Event event);
	/** Called when the processing of the given event resumes.
	* The execution can be found by {@link org.zkoss.zk.ui.Executions#getCurrent}.
	 */
	public void eventResume(Event event);
}
