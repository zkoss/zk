/* ITimerController.java

	Purpose:

	Description:

	History:
		Fri Nov 12 12:16:35 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.state;

import java.util.Objects;

import org.zkoss.lang.Strings;
import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.UiAgentCtrl;
import org.zkoss.zephyr.util.ActionHandler;
import org.zkoss.zephyr.util.Oid;
import org.zkoss.zephyr.zpr.ITimer;
import org.zkoss.zk.ui.event.Events;

/**
 * An {@link org.zkoss.zephyr.zpr.ITimer} controller to control start, stop, and
 * some status with the given timer instance.
 *
 * <p>Note: this class is not thread-safe, so when it's used in multi-threading
 * environment, the developer should handle the threading issue.</p>
 *
 * @author katherine
 */
public class ITimerController {
	private ITimer _owner;
	private boolean _isRunning;
	final private Locator _locator;

	private ITimerController(ITimer owner) {
		Objects.requireNonNull(owner);
		ITimer.Builder builder = new ITimer.Builder().from(owner);

		if (Strings.isEmpty(owner.getId())) {
			builder.setId(Oid.generate(owner));
		} else {
			builder.setId(owner.getId());
		}

		// avoid to use setActions() or setAction() to override the original action handlers.
		_owner = builder.addActions(ActionHandler.of(this::doTimer)).build();
		_isRunning = _owner.isRunning();
		_locator = Locator.of(_owner);
	}

	/** Starts the timer.
	 */
	public void start() {
		if (!_isRunning) {
			_isRunning = true;
			UiAgentCtrl.smartUpdate(_locator, "running", true);
		}
	}

	/** Stops the timer.
	 */
	public void stop() {
		if (_isRunning) {
			_isRunning = false;
			UiAgentCtrl.smartUpdate(_locator, "running", false);
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Action(type = Events.ON_TIMER)
	public void doTimer() {
		if (!_owner.isRepeats()) {
			_isRunning = false;
		}
	}

	/**
	 * Returns the controller instance with the given {@link ITimer timer}
	 * @param owner The controller to control with
	 */
	public static ITimerController of(ITimer owner) {
		return new ITimerController(owner);
	}

	/**
	 * Returns the immutable timer instance that the controller to build with.
	 */
	public ITimer build() {
		return _owner.withRunning(_isRunning);
	}
}
