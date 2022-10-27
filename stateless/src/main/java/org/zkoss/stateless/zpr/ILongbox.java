/* ILongbox.java

	Purpose:

	Description:

	History:
		Wed Oct 27 15:46:01 CST 2021, Created by katherine

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
import org.zkoss.zul.Longbox;

/**
 * Immutable {@link Longbox} component.
 *
 * <p>An edit box for holding a large integer.</p>
 *
 * <h2>Constraint</h2>
 * <p>You could specify what value to accept for input controls by use of the
 * {@link #withConstraint(String) constraint} attribute. It could be a combination
 * of {@code no positive}, {@code no negative}, {@code no zero}, and {@code no empty}.</p>
 *
 * @author katherine
 * @see Longbox
 */
@StatelessStyle
public interface ILongbox extends INumberInputElement<ILongbox, Long>, IAnyGroup<ILongbox> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ILongbox DEFAULT = new ILongbox.Builder().build();

	// override super's method for generate Javadoc in Updater
	ILongbox withValue(Long value);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Longbox> getZKType() {
		return Longbox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.inp.Longbox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.inp.Longbox";
	}

	/**
	 * Internal use
	 * @param value the value to be sent to the client
	 * @hidden
	 */
	@Value.Lazy
	default Object marshall(Object value) {
		return value != null ? ((Long) value).toString() : value;
	}

	/**
	 * Returns the instance with the given value.
	 * @param value The long value
	 */
	static ILongbox of(Long value) {
		return new ILongbox.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given value.
	 * @param value The long value
	 */
	static ILongbox of(long value) {
		return new ILongbox.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given cols.
	 * @param cols The cols which determines the visible width
	 */
	static ILongbox ofCols(int cols) {
		return new ILongbox.Builder().setCols(cols).build();
	}

	/**
	 * Returns the instance with the given constraint.
	 * @param constraint The intbox constraint
	 */
	static ILongbox ofConstraint(String constraint) {
		Objects.requireNonNull(constraint, "Cannot allow null");
		return new Builder().setConstraint(constraint).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ILongbox ofId(String id) {
		return new ILongbox.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		// refer to Longbox, change Long to String.
		INumberInputElement.super.renderProperties(renderer);
	}

	/**
	 * Builds instances of type {@link ILongbox ILongbox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableILongbox.Builder {
	}

	/**
	 * Builds an updater of type {@link ILongbox} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ILongboxUpdater {}
}