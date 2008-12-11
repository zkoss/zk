/* Timer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep 26 12:45:22     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;

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
public class Timer extends HtmlBasedComponent implements org.zkoss.zul.api.Timer {
	private int _delay;
	private boolean _repeats, _running = true;

	static {
		addClientEvent(Timer.class, Events.ON_TIMER);
	}

	public Timer() {
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
			smartUpdate("delay", _delay);
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
			smartUpdate("repeats", _repeats);
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
			smartUpdate("running", false);
		}
	}
	/** Starts the timer.
	 */
	public void start() {
		if (!_running) {
			_running = true;
			smartUpdate("running", true);
		}
	}

	//-- Component --//
	/** Not childable. */
	protected boolean isChildable() {
		return false;
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link HtmlBasedComponent#process},
	 * it also handles onOpen.
	 * @since 5.0.0
	 */
	public void process(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String name = request.getName();
		if (name.equals(Events.ON_TIMER)) {
			if (!_repeats) _running = false; //Bug 1829397
		}
		super.process(request, everError);
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "repeats", _repeats);
		if (_delay != 0) renderer.render("delay", _delay);
		if (!_running) render(renderer, "running", false);
	}
}
