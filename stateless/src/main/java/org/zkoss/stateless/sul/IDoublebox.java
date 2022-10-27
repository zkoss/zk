/* IDoublebox.java

	Purpose:

	Description:

	History:
		Tue Oct 26 15:18:45 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Doublebox;

/**
 * Immutable {@link Doublebox} component.
 *
 * <p> An edit box for holding an float point value (double).</p>
 *
 * <h2>Constraint</h2>
 * <p>You could specify what value to accept for input controls by use of the
 * {@link #withConstraint(String) constraint} attribute. It could be a combination
 * of {@code no positive}, {@code no negative}, {@code no zero}, and {@code no empty}.</p>
 *
 * @author katherine
 * @see Doublebox
 */
@StatelessStyle
public interface IDoublebox extends INumberInputElement<IDoublebox, Double>,
		IAnyGroup<IDoublebox> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IDoublebox DEFAULT = new IDoublebox.Builder().build();

	// override super's method for generate Javadoc in Updater
	IDoublebox withValue(Double value);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Doublebox> getZKType() {
		return Doublebox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.inp.Doublebox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.inp.Doublebox";
	}

	/**
	 * Returns the instance with the given value.
	 * @param value The double value
	 */
	static IDoublebox of(Double value) {
		return new IDoublebox.Builder().setValue(value).build();
	}
	/**
	 * Returns the instance with the given value.
	 * @param value The double value
	 */
	static IDoublebox of(double value) {
		return new IDoublebox.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given cols.
	 * @param cols The cols which determines the visible width
	 */
	static IDoublebox ofCols(int cols) {
		return new IDoublebox.Builder().setCols(cols).build();
	}

	/**
	 * Returns the instance with the given constraint.
	 * @param constraint The doublebox constraint
	 */
	static IDoublebox ofConstraint(String constraint) {
		Objects.requireNonNull(constraint, "Cannot allow null");
		return new Builder().setConstraint(constraint).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IDoublebox ofId(String id) {
		return new IDoublebox.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		INumberInputElement.super.renderProperties(renderer);
	}

	/**
	 * Builds instances of type {@link IDoublebox IDoublebox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIDoublebox.Builder {
	}

	/**
	 * Builds an updater of type {@link IDoublebox} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IDoubleboxUpdater {}
}