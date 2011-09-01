/* ScalableTimerTask.java

	Purpose:
		
	Description:
		
	History:
		Wed Dec  5 14:31:40     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.TimerTask;

/**
 * A task that can be scheduled for one-time execution by
 * a scalable timer ({@link ScalableTimer}.
 *
 * @author tomyeh
 * @since 3.0.1
 */
abstract public class ScalableTimerTask extends TimerTask {
	private ScalableTimerInfo _ti;

	/*package*/ void setScalableTimerInfo(ScalableTimerInfo ti) {
		_ti = ti;
	}

	/** The action to be performed by this timer task.
	 * The derived class must override this method instead of {@link #run}.
	 */
	abstract public void exec();

	//super//
	/** Invokes {@link #exec}.
	 * The derived class shall not override this method.
	 * Rather, override {@link #exec} instead.
	 */
	final public void run() {
		setCalled();
		exec();
	}
	/** Cancels this timer task.
	 *
	 * @return true if this task is scheduled for one-time execution and has not yet run.
	 * Returns false if the task was scheduled for one-time execution and has already run.
	 */
	public boolean cancel() {
		final boolean b = super.cancel();
		if (b) setCalled();
		return b;
	}
	private void setCalled() {
		if (_ti != null) {
			synchronized (_ti) {
				--_ti.count;
			}
			_ti = null;
		}
	}
}
