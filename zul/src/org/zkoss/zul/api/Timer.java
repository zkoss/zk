/* Timer.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;

/**
 * Fires one or more {@link org.zkoss.zk.ui.event.Event} after a specified
 * delay.
 * 
 * <p>
 * {@link Timer} is a special component that is invisible.
 * 
 * <p>
 * Notice that the timer won't fire any event until it is attached to a page.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Timer extends org.zkoss.zk.ui.api.HtmlBasedComponent {

	/**
	 * Returns the delay, the number of milliseconds between successive action
	 * events.
	 * <p>
	 * Default: 0 (immediately).
	 */
	public int getDelay();

	/**
	 * Sets the delay, the number of milliseconds between successive action
	 * events.
	 */
	public void setDelay(int delay) throws WrongValueException;

	/**
	 * Returns whether the timer shall send Event repeatly.
	 * <p>
	 * Default: false.
	 */
	public boolean isRepeats();

	/**
	 * Sets whether the timer shall send Event repeatly.
	 */
	public void setRepeats(boolean repeats);

	/**
	 * Returns whether this timer is running.
	 * <p>
	 * Default: true.
	 * 
	 * @see #stop
	 * @see #start
	 */
	public boolean isRunning();

	/**
	 * Start or stops the timer.
	 */
	public void setRunning(boolean running);

	/**
	 * Stops the timer.
	 */
	public void stop();

	/**
	 * Starts the timer.
	 */
	public void start();

}
