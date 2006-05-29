/* Timer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep 26 12:45:22     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.xml.HTMLs;

import com.potix.zk.ui.WrongValueException;
import com.potix.zk.au.AuInit;
import com.potix.zk.au.AuCleanup;
import com.potix.zul.html.impl.XulElement;

/**
 * Fires one or more {@link com.potix.zk.ui.event.Event} after
 * a specified delay.
 *
 * <p>{@link Timer} is a special component that is invisible.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.8 $ $Date: 2006/05/29 04:28:27 $
 */
public class Timer extends XulElement {
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
			smartUpdate("zk_deplay", Integer.toString(_delay));
			if (_running)
				response("init", new AuInit(this));
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
			smartUpdate("zk_repeats", Boolean.toString(_repeats));
			if (_running)
				response("init", new AuInit(this));
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
	/** Stops the timer.
	 */
	public void stop() {
		if (_running) {
			_running = false;
			response(null, new AuCleanup(this));
		}
	}
	/** Starts the timer.
	 */
	public void start() {
		if (!_running) {
			_running = true;
			response("init", new AuInit(this));
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		HTMLs.appendAttribute(sb, "zk_delay", _delay);
		HTMLs.appendAttribute(sb, "zk_repeats", _repeats);
		return sb.toString();
	}

	//-- Component --//
	/** Not allowd. */
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("Timer is always invisible");
	}
	/** Not childable. */
	public boolean isChildable() {
		return false;
	}
}
