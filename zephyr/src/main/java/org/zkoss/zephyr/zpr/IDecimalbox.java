/* IDecimalbox.java

	Purpose:

	Description:

	History:
		Wed Oct 27 15:48:07 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Decimalbox;

/**
 * Immutable {@link Decimalbox} component.
 *
 * <p>An edit box for holding BigDecimal.</p>
 *
 * <h2>Constraint</h2>
 * <p>You could specify what value to accept for input controls by use of the
 * {@link #withConstraint(String) constraint} attribute. It could be a combination
 * of {@code no positive}, {@code no negative}, {@code no zero}, and {@code no empty}.</p>
 *
 * @author katherine
 * @see Decimalbox
 */
@ZephyrStyle
public interface IDecimalbox extends INumberInputElement<IDecimalbox, BigDecimal>,
		IAnyGroup<IDecimalbox> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IDecimalbox DEFAULT = new IDecimalbox.Builder().build();

	// override super's method for generate Javadoc in Updater
	IDecimalbox withValue(BigDecimal value);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Decimalbox> getZKType() {
		return Decimalbox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.inp.Decimalbox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.inp.Decimalbox";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Object marshall(Object value) {
		return value != null ? ((BigDecimal) value).toPlainString() : value;
	}

	/** Returns the scale for the decimal number storing in this component,
	 * or {@link Decimalbox#AUTO} if the scale is decided automatically (based on
	 * what user has entered).
	 *
	 * <p>Default: {@link Decimalbox#AUTO}.
	 */
	default int getScale() {
		return Decimalbox.AUTO;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code scale}.
	 *
	 * <p>Sets the scale for the decimal number storing in this component,
	 * or {@link Decimalbox#AUTO} if the scale is decided automatically (based on
	 * what user has entered).
	 *
	 * <p>For example, set the scale of 1234.1234 to 2, the result will be 1234.12
	 *
	 * @param scale The scale for the decimal number storing in this component
	 * <p>Default: {@link Decimalbox#AUTO}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDecimalbox withScale(int scale);

	/**
	 * Returns the instance with the given value.
	 * @param value The BigDecimal value
	 */
	static IDecimalbox of(BigDecimal value) {
		return new IDecimalbox.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given value.
	 * @param value The string value of the decimalbox
	 */
	static IDecimalbox of(String value) {
		return new IDecimalbox.Builder().setValue(value == null ? null : new BigDecimal(value)).build();
	}

	/**
	 * Returns the instance with the given cols.
	 * @param cols The cols which determines the visible width
	 */
	static IDecimalbox ofCols(int cols) {
		return new IDecimalbox.Builder().setCols(cols).build();
	}

	/**
	 * Returns the instance with the given constraint.
	 * @param constraint The decimalbox constraint
	 */
	static IDecimalbox ofConstraint(String constraint) {
		Objects.requireNonNull(constraint, "Cannot allow null");
		return new IDecimalbox.Builder().setConstraint(constraint).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IDecimalbox ofId(String id) {
		return new IDecimalbox.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		INumberInputElement.super.renderProperties(renderer);

		int _scale = getScale();
		if (_scale != Decimalbox.AUTO)
			renderer.render("scale", _scale);
	}

	/**
	 * Builds instances of type {@link IDecimalbox IDecimalbox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIDecimalbox.Builder {
	}

	/**
	 * Builds an updater of type {@link IDecimalbox} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IDecimalboxUpdater {}
}