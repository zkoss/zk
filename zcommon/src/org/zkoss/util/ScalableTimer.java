/* ScalableTimer.java

	Purpose:
		
	Description:
		
	History:
		Wed Dec  5 14:09:13     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.Date;
import java.util.Timer;

/**
 * A facility for threads to schedule tasks for future execution in
 * a background thread.
 * It has the same function as {@link Timer}, but it is scalable by use of
 * a collection of {@link Timer}. Use {@link ScalableTimer} if you want to
 * schedule a lot of tasks.
 * However, it doesn't support repeated execution.
 *
 * @author tomyeh
 * @since 3.0.1
 */
public class ScalableTimer {
	private final ScalableTimerInfo[] _tis;
	private int _threshold;

	/** Creates a scalable timer.
	 *
	 * <p>Note: no real timer (thread) is created until the first task
	 * being scheduled.
	 *
	 * @param cTimers the maximal allowed number of timers to use.
	 * 1 is assumed if nonpositive.
	 * @param threshold the threshold, i.e., the number of tasks allowed to
	 * be scheduled for a timer before creating additional timer.
	 * 1 is assumed if nonpositive.
	 * Note: if the number of scheduled task exceeds the capacity
	 * ({@link #getMaxTimerSize} * {@link #getThreshold}), the tasks are distributed evenly
	 * over all timers
	 */
	public ScalableTimer(int cTimers, int threshold) {
		if (cTimers <= 0) cTimers = 1;
		if (threshold <= 0) threshold = 1;

		_tis = new ScalableTimerInfo[cTimers];
		for (int j = cTimers; --j >= 0;)
			_tis[j] = new ScalableTimerInfo();
		_threshold = threshold;
	}

	/** Returns the maximal allowed number of timers ({@link Timer})
	 * are used in this instance.
	 */
	public int getMaxTimerSize() {
		return _tis.length;
	}
	/** Returns the threshold, i.e., the allowed number of tasks to be
	 * scheduled for a timer, before starting a new timer.
	 */
	public int getThreshold() {
		return _threshold;
	}

	/** Terminates this timer, discarding any currently scheduled tasks.
	 * This method may be called repeatedly; the second and subsequent calls have no effect.
	 */
	public void cancel() {
		for (int j = _tis.length; --j >= 0;) {
			final ScalableTimerInfo ti = _tis[j];
			synchronized (ti) {
				if (ti.timer != null) {
					ti.timer.cancel();
					ti.timer = null;
				}
				ti.count = 0;
			}
		}
	}
 
	/** Schedules the specified task for execution after the specified delay.
	 * @param task task to be scheduled.
	 * @param delay delay in milliseconds before task is to be executed.
	 */
	public void schedule(ScalableTimerTask task, long delay) {
		getInfo(task).timer.schedule(task, delay);
	}
	/** Schedules the specified task for execution at the specified time.
	 * @param task task to be scheduled.
     * @param time time at which task is to be executed.
	 */
	public void schedule(ScalableTimerTask task, Date time) {
		getInfo(task).timer.schedule(task, time);
	}

	private ScalableTimerInfo getInfo(ScalableTimerTask task) {
		//No sync to select a proper ti since it is ok to race
		ScalableTimerInfo ti = null;
		for (int j = _tis.length; --j >= 0;) {
			if (_tis[j].count < _threshold) {
				ti = _tis[j];
				break; //found
			}
			if (ti == null || ti.count > _tis[j].count)
				ti = _tis[j];
		}

		synchronized (ti) {
			++ti.count;
			if (ti.timer == null)
				ti.timer = new Timer(true);
		}
		task.setScalableTimerInfo(ti);
		return ti;
	}
}
