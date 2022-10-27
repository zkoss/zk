/* IIntbox.java

	Purpose:

	Description:

	History:
		Tue Oct 26 12:56:16 CST 2021, Created by katherine

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
import org.zkoss.zul.Intbox;

/**
 * Immutable {@link Intbox} component
 * <p>An edit box for holding an integer.</p>
 *
 * <h2>Constraint</h2>
 * <p>You could specify what value to accept for input controls by use of the
 * {@link #withConstraint(String) constraint} attribute. It could be a combination
 * of {@code no positive}, {@code no negative}, {@code no zero}, and {@code no empty}.</p>
 *
 * @author katherine
 * @see Intbox
 */
@StatelessStyle
public interface IIntbox extends INumberInputElement<IIntbox, Integer>, IAnyGroup<IIntbox> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IIntbox DEFAULT = new Builder().build();

	// override super's method for generate Javadoc in Updater
	IIntbox withValue(Integer value);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Intbox> getZKType() {
		return Intbox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.inp.Intbox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.inp.Intbox";
	}

	/**
	 * Returns the instance with the given value.
	 * @param value The integer value
	 */
	static IIntbox of(Integer value) {
		return new IIntbox.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given value.
	 * @param value The integer value
	 */
	static IIntbox of(int value) {
		return new IIntbox.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given cols.
	 * @param cols The cols which determines the visible width
	 */
	static IIntbox ofCols(int cols) {
		return new IIntbox.Builder().setCols(cols).build();
	}

	/**
	 * Returns the instance with the given constraint.
	 * @param constraint The intbox constraint
	 */
	static IIntbox ofConstraint(String constraint) {
		Objects.requireNonNull(constraint, "Cannot allow null");
		return new Builder().setConstraint(constraint).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IIntbox ofId(String id) {
		return new IIntbox.Builder().setId(id).build();
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
	 * Builds instances of type {@link IIntbox IIntbox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIIntbox.Builder {
	}

	/**
	 * Builds an updater of type {@link IIntbox} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IIntboxUpdater {}
}