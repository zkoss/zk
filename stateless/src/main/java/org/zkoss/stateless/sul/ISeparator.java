/* ISeparator.java

	Purpose:

	Description:

	History:
		Fri Oct 29 14:10:38 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.stateless.ui.util.IComponentChecker;
import org.zkoss.zul.Separator;

/**
 * Immutable {@link org.zkoss.zul.Separator} component
 *
 * <p>
 * A separator.
 * </p>
 * @author katherine
 * @see Separator
 */
@StatelessStyle
public interface ISeparator extends ISeparatorBase<ISeparator>, IAnyGroup<ISeparator> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ISeparator DEFAULT = new ISeparator.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Separator> getZKType() {
		return Separator.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Separator"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Separator";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkOrient() {
		IComponentChecker.checkOrient(getOrient());
	}

	/**
	 * Returns the instance with the given width.
	 * @param width The width of this component.
	 */
	static ISeparator ofWidth(String width) {
		return new ISeparator.Builder().setWidth(width).build();
	}

	/**
	 * Returns the instance with the given orient.
	 * @param orient The separator orient
	 */
	static ISeparator ofOrient(ISeparatorBase.Orient orient) {
		return new ISeparator.Builder().setOrient(orient.value).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ISeparator ofId(String id) {
		return new ISeparator.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link ISeparator ISeparator}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableISeparator.Builder {
	}

	/**
	 * Builds an updater of type {@link ISeparator} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ISeparatorUpdater {}
}