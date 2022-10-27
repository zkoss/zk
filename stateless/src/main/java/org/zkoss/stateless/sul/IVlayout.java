/* IVlayout.java

	Purpose:

	Description:

	History:
		Tue Oct 19 11:39:14 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Vlayout;

/**
 * Immutable {@link Vlayout} component
 *
 * <p>The vlayout component is a simple vertical oriented layout.
 * Added components will be placed underneath each other in a column.</p>
 *
 * @author katherine
 * @see Vlayout
 */
@StatelessStyle
public interface IVlayout<I extends IAnyGroup> extends ILayout<IVlayout<I>>,
		IChildable<IVlayout<I>, I>, IAnyGroup<IVlayout<I>> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IVlayout<IAnyGroup> DEFAULT = new IVlayout.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Vlayout> getZKType() {
		return Vlayout.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.box.Vlayout"}</p>
	 */
	default String getWidgetClass() {
		return "zul.box.Vlayout";
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IVlayout<I> ofId(String id) {
		return new IVlayout.Builder<I>().setId(id).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IVlayout<I> of(Iterable<? extends I> children) {
		return new IVlayout.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IVlayout<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given hflex.
	 * @param hflex The horizontal flex hint.
	 */
	static <I extends IAnyGroup> IVlayout<I> ofHflex(String hflex) {
		return new Builder<I>().setHflex(hflex).build();
	}

	/**
	 * Builds instances of type {@link IVlayout IVlayout}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIVlayout.Builder<I> {}

	/**
	 * Builds an updater of type {@link IVlayout} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IVlayoutUpdater {}
}