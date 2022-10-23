/* ITimer.java

	Purpose:

	Description:

	History:
		Thu Nov 04 17:59:15 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Timer;

/**
 * Immutable {@link Timer} component
 *
 * <p> Fires one or more {@link org.zkoss.zk.ui.event.Event} after
 * a specified delay.
 *
 * <p>{@link ITimer} is a special component that is invisible.
 * </p>
 *
 * <h3>Support {@literal @}Action</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Action Type</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>onTimer</td>
 *          <td>Denotes the timer you specified has triggered an action. To know
 *          which timer.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Timer
 */
@ZephyrStyle
public interface ITimer extends IHtmlBasedComponent<ITimer>, IAnyGroup<ITimer> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITimer DEFAULT = new ITimer.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Timer> getZKType() {
		return Timer.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.utl.Timer"}</p>
	 */
	default String getWidgetClass() {
		return "zul.utl.Timer";
	}

	/** Returns whether the timer shall send Event repeatedly.
	 * <p>Default: {@code false}.
	 */
	default boolean isRepeats() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code repeats}.
	 *
	 * <p>Sets whether the timer shall send Event repeatedly.
	 *
	 * @param repeats Whether the timer shall send Event repeatedly.
	 * @return A modified copy of {@code this} object
	 */
	ITimer withRepeats(boolean repeats);

	/** Returns whether this timer is running.
	 * <p>Default: {@code true}.
	 */
	default boolean isRunning() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code running}.
	 *
	 * <p>Sets whether this timer is running.
	 *
	 * @param running Whether this timer is running.
	 * @return A modified copy of {@code this} object
	 */
	ITimer withRunning(boolean running);

	/** Returns the delay, the number of milliseconds between
	 * successive action events.
	 * <p>Default: {@code 0} (immediately).
	 */
	default int getDelay() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code delay}.
	 *
	 * <p>Sets the delay, the number of milliseconds between
	 * successive action events.
	 *
	 * @param delay If negative, 0 is assumed.
	 * @return A modified copy of {@code this} object
	 */
	ITimer withDelay(int delay);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ITimer checkDelay() {
		int delay = getDelay();
		if (delay < 0) {
			return withDelay(0);
		}
		return this;
	}

	/**
	 * Returns the instance with the given delay.
	 * @param delay If negative, 0 is assumed.
	 */
	static ITimer ofDelay(int delay) {
		return new ITimer.Builder().setDelay(delay).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ITimer ofId(String id) {
		return new ITimer.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IHtmlBasedComponent.super.renderProperties(renderer);

		render(renderer, "repeats", isRepeats());
		int _delay = getDelay();
		if (_delay != 0)
			renderer.render("delay", _delay);
		if (!isRunning())
			renderer.render("running", false);
	}

	/**
	 * Builds instances of type {@link ITimer ITimer}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITimer.Builder {
	}

	/**
	 * Builds an updater of type {@link ITimer} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ITimerUpdater {}
}