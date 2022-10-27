/* IBandpopup.java

	Purpose:

	Description:

	History:
		Fri Oct 08 15:10:23 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Bandpopup;

/**
 * Immutable {@link Bandpopup} component
 *
 * <p>The popup that belongs to a {@link IBandbox} instance.
 * <p>Developer usually listens to the {@code onOpen} action that is sent to
 * {@link IBandbox} and then creates proper components as children
 * of this component.</p>
 *
 * @author katherine
 * @see Bandpopup
 */
@StatelessStyle
public interface IBandpopup<I extends IAnyGroup> extends IXulElement<IBandpopup<I>>,
		IChildable<IBandpopup<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IBandpopup DEFAULT = new IBandpopup.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Bandpopup> getZKType() {
		return Bandpopup.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.inp.Bandpopup"}</p>
	 */
	default String getWidgetClass() {
		return "zul.inp.Bandpopup";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkVisible() {
		if (!this.isVisible())
			throw new UnsupportedOperationException("Use Bandbox.setOpen(false) instead");
	}

	/**
	 *
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IBandpopup<I> of(Iterable<? extends I> children) {
		return new IBandpopup.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IBandpopup<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IBandpopup<I> ofId(String id) {
		return new IBandpopup.Builder<I>().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IBandpopup IBandpopup}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIBandpopup.Builder<I> {}

	/**
	 * Builds an updater of type {@link IBandpopup} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IBandpopupUpdater {}
}