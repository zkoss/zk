/* Timer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep 26 12:45:22     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.HtmlBasedComponent;

/**
 * Fires one or more {@link org.zkoss.zk.ui.event.Event} after
 * a specified delay.
 *
 * <p>{@link Timer} is a special component that is invisible.
 *
 * <p>Notice that the timer won't fire any event until it is attached
 * to a page.
 *
 * @author tomyeh
 */
public class Timer extends HtmlBasedComponent {
	private int _delay;
	private boolean _repeats, _running = true;

	public Timer() {
		super.setVisible(false);
	}
	public Timer(int delay) {
		this();
		_delay = delay;
	}

	/** Returns the delay, the number of milliseconds between
	 * successive action events.
	 * <p>Default: 0 (immediately).
	 */
	public int getDelay() {
		return _delay;
	}
	/** Sets the delay, the number of milliseconds between
	 * successive action events.
	 */
	public void setDelay(int delay)
	throws WrongValueException {
		if (delay < 0)
			throw new WrongValueException("Negative delay is not allowed: "+delay);
		if (delay != _delay) {
			_delay = delay;
			smartUpdate("z.delay", Integer.toString(_delay));
			if (_running)
				smartUpdate("z.init", true); //init
		}
	}
	/** Returns whether the timer shall send Event repeatly.
	 * <p>Default: false.
	 */
	public boolean isRepeats() {
		return _repeats;
	}
	/** Sets whether the timer shall send Event repeatly.
	 */
	public void setRepeats(boolean repeats) {
		if (_repeats != repeats) {
			_repeats = repeats;
			smartUpdate("z.repeats", Boolean.toString(_repeats));
			if (_running)
				smartUpdate("z.init", true); //init
		}
	}
	/** Returns whether this timer is running.
	 * <p>Default: true.
	 * @see #stop
	 * @see #start
	 */
	public boolean isRunning() {
		return _running;
	}
	/** Start or stops the timer.
	 */
	public void setRunning(boolean running) {
		if (running) start();
		else stop();
	}

	/** Stops the timer.
	 */
	public void stop() {
		if (_running) {
			_running = false;
			smartUpdate("z.running", false);
		}
	}
	/** Starts the timer.
	 */
	public void start() {
		if (!_running) {
			_running = true;
			smartUpdate("z.running", true);
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		HTMLs.appendAttribute(sb, "z.delay", _delay);
		HTMLs.appendAttribute(sb, "z.repeats", _repeats);
		if (!_running)
			sb.append(" z.running=\"false\"");
		return sb.toString();
	}

	//-- Component --//
	/** Not allowd. */
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("timer is always invisible");
	}
	/** Not childable. */
	public boolean isChildable() {
		return false;
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends HtmlBasedComponent.ExtraCtrl
	implements org.zkoss.zk.ui.ext.client.Timer {
		//Timer//
		public void onTimer() {
			if (!_repeats) stop(); //Bug 1829397
		}
	}
}
