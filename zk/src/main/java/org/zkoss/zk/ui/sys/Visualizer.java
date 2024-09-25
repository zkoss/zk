/* Visualizer.java

	Purpose:

	Description:

	History:
		Fri Jan 20 23:06:03     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.List;

import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
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

	/** Returns the owner component for this execution, or null if
	 * this execution is not owned by any component.
	 * <p>The include component is a typical owner.
	 * @since 5.0.0
	 */
	public Component getOwner();

	/** Whether the execution associated with this visualizer
	 * is caused by an async-update.
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
	 * @return true if responses are added; false if the first {@link Visualizer}
	 * is NOT caused by async responses.
	 */
	public boolean addToFirstAsyncUpdate(List<AuResponse> responses);

	/** Returns whether it is in recovering.
	 * In other words, it is in the invocation of {@link FailoverManager#recover}.
	 * If in recovering, no response is sent to the client.
	 */
	public boolean isRecovering();

	/** Disables this visualizer.
	 * Once disabled, any update of components won't be synchronized to
	 * the client. In other words, it ignores any updates to components.
	 *
	 * <p>Currently, it is called only when removing a desktop.
	 * You rarely need to call this method.
	 * @since 3.0.2
	 */
	public void disable();

	/** Enables this visualizer.
	 * Once enabled, any update of components will be synchronized to
	 * the client.
	 *
	 * @since 10.0.0
	 */
	public void enable();

	/** Sets whether to disable the update of the client widget.
	 * By default, if a component is attached to a page, modifications that
	 * change the visual representation will be sent to the client to
	 * ensure the consistency.
	 *
	 * @return whether it has been disabled before this invocation, i.e.,
	 * the previous disable status
	 * @since 10.1.0
	 */
	public boolean disableClientUpdate(Component comp, boolean disable);
}
