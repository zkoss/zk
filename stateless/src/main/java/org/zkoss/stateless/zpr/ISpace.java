/* ISpace.java

	Purpose:

	Description:

	History:
		Wed Nov 03 16:06:13 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Space;

/**
 * Immutable {@link Space} component
 * <p>
 * Space is a {@link ISeparator} with the orient default to {@code "vertical"}.
 * </p>
 *
 * @author katherine
 * @see Space
 */
@StatelessStyle
public interface ISpace extends ISeparatorBase<ISpace>, IAnyGroup<ISpace> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ISpace DEFAULT = new ISpace.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Space> getZKType() {
		return Space.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Space"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Space";
	}

	/** Returns the orient.
	 * <p>Default: {@code "vertical"}.
	 */
	default String getOrient() {
		return "vertical";
	}

	/**
	 * Returns the instance with the given height.
	 * @param height The height of this component.
	 */
	static ISpace ofHeight(String height) {
		return new ISpace.Builder().setHeight(height).build();
	}

	/**
	 * Returns the instance with the given orient.
	 * @param orient The separator orient
	 */
	static ISpace ofOrient(ISeparatorBase.Orient orient) {
		return new ISpace.Builder().setOrient(orient.value).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ISpace ofId(String id) {
		return new ISpace.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link ISpace ISpace}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableISpace.Builder {
	}

	/**
	 * Builds an updater of type {@link ISpace} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ISpaceUpdater {}
}