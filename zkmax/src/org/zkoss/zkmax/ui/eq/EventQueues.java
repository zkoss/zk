/* EventQueues.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri May  2 15:30:36     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.ui.eq;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Desktop;

/**
 * Utilities to access the event queue.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public class EventQueues {
	private static final String ATTR_EVENT_QUEUES = "org.zkoss.zkmax.ui.eq.eventQueues";

	/** Returns the event queue with the specified name in the
	 * current desktop.
	 *
	 * <p>Note:
	 * <ul>
	 * <li>This method can be called only in an activated execution,
	 * i.e., {@link org.zkoss.zk.ui.Executions#getCurrent} not null.</li>
	 * <li>The event queue is associated with the desktop
	 * the current execution.</li>
	 * <li>The event queue is gone if the desktop is removed.</li>
	 * </ul>
	 *
	 * @param name the queue name.
	 * @param autoCreate whether to create the event queue if not found.
	 * @return the event queue with the associated name, or null if
	 * not found and autoCreate is false
	 * @exception IllegalStateException if not in an activated execution
	 */
	public static final EventQueue lookup(String name, boolean autoCreate) {
		final Execution exec = Executions.getCurrent();
		if (exec == null)
			throw new IllegalStateException("Not in an execution");

		final Desktop desktop = exec.getDesktop();
		Map eqs = (Map)desktop.getAttribute(ATTR_EVENT_QUEUES);
		if (eqs == null)
			desktop.setAttribute(ATTR_EVENT_QUEUES, eqs = new HashMap(4));

		EventQueue eq = (EventQueue)eqs.get(name);
		if (autoCreate && eq == null)
			eqs.put(name, eq = new EventQueueImpl());
		return eq;
	}
	/** Returns the event queue with the specified name in the current
	 * desktop, or if no such event queue, create one.
	 * It is a shortcut of <code>lookup(name, true)</code>.
	 */
	public static final EventQueue lookup(String name) {
		return lookup(name, true);
	}
}
