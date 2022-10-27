/* IMenuseparator.java

	Purpose:

	Description:

	History:
		Mon Oct 18 18:09:40 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Menuseparator;

/**
 * Immutable {@link Menuseparator} component
 *
 * <p>Used to create a separator between menu items.</p>
 *
 * @author katherine
 * @see Menuseparator
 */
@StatelessStyle
public interface IMenuseparator extends IXulElement<IMenuseparator>, IChildrenOfMenupopup<IMenuseparator> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IMenuseparator DEFAULT = new IMenuseparator.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Menuseparator> getZKType() {
		return Menuseparator.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.menu.Menuseparator"}</p>
	 */
	default String getWidgetClass() {
		return "zul.menu.Menuseparator";
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IMenuseparator ofId(String id) {
		return new IMenuseparator.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IMenuseparator IMenuseparator}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIMenuseparator.Builder {}

	/**
	 * Builds an updater of type {@link IMenuseparator} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IMenuseparatorUpdater {}
}