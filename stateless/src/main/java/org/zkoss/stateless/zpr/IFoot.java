/* IFoot.java

	Purpose:

	Description:

	History:
		Tue Dec 28 15:02:50 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Foot;

/**
 * Immutable {@link Foot} component
 *
 * @author katherine
 * @see Foot
 */
@StatelessStyle
public interface IFoot
		extends IXulElement<IFoot>, IChildable<IFoot, IFooter<IAnyGroup>>,
		IGridComposite<IFoot> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IFoot DEFAULT = new IFoot.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Foot> getZKType() {
		return Foot.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.grid.Foot"}</p>
	 */
	default String getWidgetClass() {
		return "zul.grid.Foot";
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	@Nullable
	default String getWidth() {
		return null;
	}

	/**
	 * To control the size of Foot related
	 * components, please refer to {@link IGrid} and {@link IColumn} instead.
	 */
	default IFoot withWidth(@Nullable String width) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	@Nullable
	default String getHflex() {
		return null;
	}

	/**
	 * To control the size of Foot related
	 * components, please refer to {@link IGrid} and {@link IColumn} instead.
	 */
	default IFoot withHflex(@Nullable String hflex) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Returns the instance with the given footers which belong to this component.
	 * @param children The footers of the component.
	 */
	static IFoot of(Iterable<? extends IFooter<IAnyGroup>> children) {
		return new IFoot.Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given footers which belong to this component.
	 * @param children The footers of the component.
	 */
	static IFoot of(IFooter<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}


	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IFoot ofId(String id) {
		return new IFoot.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IFoot IFoot}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIFoot.Builder {
	}

	/**
	 * Builds an updater of type {@link IFoot} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IFootUpdater {}
}