/* IDoublespinner.java

	Purpose:

	Description:

	History:
		Wed oct 27 15:42:54 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Doublespinner;

/**
 * Immutable {@link Doublespinner} component.
 *
 * <p>An edit box for holding a constrained double.</p>
 *
 * <h2>Constraint</h2>
 * <p>You could specify what value to accept for input controls by use of the
 * {@link #withConstraint(String) constraint} attribute. It could be a combination
 * of {@code no empty} and the {@code min} (minimum) and {@code max} (maximum) to doublespinner.}.
 * <br><br>
 * For example,
 * <pre>
 * <code>
 * IDoublespinner.ofConstraint("no empty, min -2.5 max 6.5: between -2.5 to 6.5");
 * </code>
 * </pre>
 * </p>
 *
 * @author katherine
 * @see Doublespinner
 */
@StatelessStyle
public interface IDoublespinner extends INumberInputElement<IDoublespinner, Double>, IAnyGroup<IDoublespinner> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IDoublespinner DEFAULT = new IDoublespinner.Builder().build();

	// override super's method for generate Javadoc in Updater
	IDoublespinner withValue(Double value);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Doublespinner> getZKType() {
		return Doublespinner.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.inp.Doublespinner"}</p>
	 */
	default String getWidgetClass() {
		return "zul.inp.Doublespinner";
	}

	/**
	 * Returns the step of doublespinner
	 * <p>Default: {@code 1.0}</p>
	 */
	default double getStep() {
		return 1.0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code step}.
	 *
	 * <p>Sets the step of doublespinner
	 *
	 * @param step The step of the doublespinner
	 * <p>Default: {@code 1.0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDoublespinner withStep(double step);

	/** Returns whether the button (on the right of the doublespinner) is visible.
	 * <p>Default: {@code true}.
	 */
	default boolean isButtonVisible() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code buttonVisible}.
	 *
	 * <p>Sets whether the button (on the right of the doublespinner) is visible.
	 *
	 * @param buttonVisible {@code false} to disable the button visibility.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDoublespinner withButtonVisible(boolean buttonVisible);

	/**
	 * Returns the instance with the given value.
	 * @param value The double value of the spinner
	 */
	static IDoublespinner of(Double value) {
		return new IDoublespinner.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given value.
	 * @param value The double value of the spinner
	 */
	static IDoublespinner of(double value) {
		return new IDoublespinner.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given cols.
	 * @param cols The cols which determines the visible width
	 */
	static IDoublespinner ofCols(int cols) {
		return new IDoublespinner.Builder().setCols(cols).build();
	}

	/**
	 * Returns the instance with the given constraint.
	 * @param constraint The doublespinner constraint
	 */
	static IDoublespinner ofConstraint(String constraint) {
		Objects.requireNonNull(constraint, "Cannot allow null");
		return new Builder().setConstraint(constraint).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IDoublespinner ofId(String id) {
		return new IDoublespinner.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		INumberInputElement.super.renderProperties(renderer);
		double _step = getStep();
		if (Double.compare(_step, 1.0) != 0)
			renderer.render("step", _step);
		boolean _btnVisible = isButtonVisible();
		if (!_btnVisible)
			renderer.render("buttonVisible", _btnVisible);
	}

	/**
	 * Builds instances of type {@link IDoublespinner IDoublespinner}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIDoublespinner.Builder {
	}

	/**
	 * Builds an updater of type {@link IDoublespinner} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IDoublespinnerUpdater {}
}