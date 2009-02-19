/* Visualizer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 20 23:06:03     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.List;
import java.io.Writer;

import org.zkoss.zk.ui.Execution;

/**
 * A part of {@link Execution} for visualizing the components whose
 * visual parts are modified. There is one-to-one relationship between
 * the visualizer and the execution.
 *
 * <p>Its implementation highly depends on {@link UiEngine}.
 *
 * @author tomyeh
 */
public interface Visualizer {
	/** Returns the execution that this visualizer is associated with.
	 */
	public Execution getExecution();
	/** Whether the execution associated with this visualizer
	 * is caued by an async-update.
	 *
	 * <p>Note: There might be a chain of executions.
	 * {@link Execution#isAsyncUpdate} returns whether a page is
	 * in async-update in the execution being called,
	 * On the other hand, this method returns whether
	 * this first execution (of the whole chain) is caused by
	 * async-update.
	 */
	public boolean isEverAsyncUpdate();
	/** Adds the responses to the first {@link Visualizer} if it is
	 * caused by async-update.
	 * @return true if respones are added; false if the first {@link Visualizer}
	 * is NOT caused by assync responses.
	 */
	public boolean addToFirstAsyncUpdate(List responses);

	/** Returns whether it is in recovering.
	 * In other words, it is in the invocation of {@link FailoverManager#recover}.
	 * If in recovering, no response is sent to the client.
	 */
	public boolean isRecovering();

	/** Disables this visualizer.
	 * Once disabled, any update of components won't be synchroized to
	 * the client. In other words, it ignores any updates to components.
	 *
	 * <p>Currently, it is called only when removing a desktop.
	 * You rarely need to call this method.
	 * @since 3.0.2
	 */
	public void disable();
}
