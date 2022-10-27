/* IProgressmeter.java

	Purpose:

	Description:

	History:
		Thu Dec 16 10:58:17 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Progressmeter;

/**
 * Immutable {@link Progressmeter} component
 *
 * <p>A progress meter is a bar that indicates how much of a task has been completed. </p>
 *
 * @author katherine
 * @see Progressmeter
 */
@StatelessStyle
public interface IProgressmeter extends IXulElement<IProgressmeter>, IAnyGroup<IProgressmeter> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IProgressmeter DEFAULT = new IProgressmeter.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Progressmeter> getZKType() {
		return Progressmeter.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code zul.wgt.Progressmeter"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Progressmeter";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkValue() {
		int value = getValue();
		if (value < 0 || value > 100)
			throw new UiException("Illegal value: " + value + ". Range: 0 ~ 100");
	}

	/** Returns the current value of the progress meter.
	 */
	default int getValue() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code value}.
	 *
	 * <p> Sets the current value of the progress meter.
	 *
	 * @param value The current value of the progress meter.
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IProgressmeter withValue(int value);

	/** Returns the indeterminate state of the progress meter.(default false)
	 */
	default boolean isIndeterminate() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code indeterminate}.
	 *
	 * <p> Sets the indeterminate state of the progress meter.
	 *
	 * @param indeterminate The indeterminate state of the progress meter.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IProgressmeter withIndeterminate(boolean indeterminate);

	/**
	 * Returns the width.
	 * <p>Default: {@code "100px"}</p>
	 */
	default String getWidth() {
		return "100px";
	}

	/**
	 * Returns the instance with the given value.
	 * @param value The current value of the progress meter.
	 * @return
	 */
	static IProgressmeter of(int value) {
		return new IProgressmeter.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given width.
	 * @param width The width of the component.
	 */
	static IProgressmeter ofWidth(String width) {
		return new IProgressmeter.Builder().setWidth(width).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IProgressmeter ofId(String id) {
		return new IProgressmeter.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);
		render(renderer, "value", "" + getValue());
		render(renderer, "indeterminate", isIndeterminate());
	}

	/**
	 * Builds instances of type {@link IProgressmeter IProgressmeter}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIProgressmeter.Builder {
	}

	/**
	 * Builds an updater of type {@link IProgressmeter} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IProgressmeterUpdater {}
}