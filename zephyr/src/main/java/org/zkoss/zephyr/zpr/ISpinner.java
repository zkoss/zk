/* ISpinner.java

	Purpose:

	Description:

	History:
		Wed Oct 27 15:28:00 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Spinner;

/**
 * Immutable {@link Spinner} component
 *
 * <p>An edit box for holding a constrained integer.</p>
 *
 * <h2>Constraint</h2>
 * <p>You could specify what value to accept for input controls by use of the
 * {@link #withConstraint(String) constraint} attribute. It could be a combination
 * of {@code no empty} and the {@code min} (minimum) and {@code max} (maximum) to spinner.}.
 * <br><br>
 * For example,
 * <pre>
 * <code>
 * ISpinner.ofConstraint("no empty, min -2 max 6: between -2 to 6");
 * </code>
 * </pre>
 * </p>
 * @author katherine
 * @see Spinner
 */
@ZephyrStyle
public interface ISpinner extends INumberInputElement<ISpinner, Integer>, IAnyGroup<ISpinner> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ISpinner DEFAULT = new ISpinner.Builder().build();

	// override super's method for generate Javadoc in Updater
	ISpinner withValue(Integer value);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Spinner> getZKType() {
		return Spinner.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.inp.Spinner"}</p>
	 */
	default String getWidgetClass() {
		return "zul.inp.Spinner";
	}

	/**
	 * Returns the step of spinner
	 * <p>Default: {@code 1}</p>
	 */
	default int getStep() {
		return 1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code step}.
	 *
	 * <p>Sets the step of spinner
	 *
	 * @param step The step of the spinner
	 * <p>Default: {@code 1}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISpinner withStep(int step);

	/** Returns whether the button (on the right of the spinner) is visible.
	 * <p>Default: {@code true}.
	 */
	default boolean isButtonVisible() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code buttonVisible}.
	 *
	 * <p>Sets whether the button (on the right of the spinner) is visible.
	 *
	 * @param buttonVisible {@code false} to disable the button visibility.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ISpinner withButtonVisible(boolean buttonVisible);

	/**
	 * Returns the instance with the given value.
	 * @param value The integer value of the spinner
	 */
	static ISpinner of(Integer value) {
		return new ISpinner.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given value.
	 * @param value The integer value of the spinner
	 */
	static ISpinner of(int value) {
		return new ISpinner.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given cols.
	 * @param cols The cols which determines the visible width
	 */
	static ISpinner ofCols(int cols) {
		return new ISpinner.Builder().setCols(cols).build();
	}

	/**
	 * Returns the instance with the given constraint.
	 * @param constraint The spinner constraint
	 */
	static ISpinner ofConstraint(String constraint) {
		Objects.requireNonNull(constraint, "Cannot allow null");
		return new Builder().setConstraint(constraint).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ISpinner ofId(String id) {
		return new ISpinner.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		INumberInputElement.super.renderProperties(renderer);
		int _step = getStep();
		if (_step != 1)
			renderer.render("step", _step);
		boolean _btnVisible = isButtonVisible();
		if (!_btnVisible)
			renderer.render("buttonVisible", _btnVisible);
	}

	/**
	 * Builds instances of type {@link ISpinner ISpinner}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableISpinner.Builder {
	}

	/**
	 * Builds an updater of type {@link ISpinner} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ISpinnerUpdater {}
}