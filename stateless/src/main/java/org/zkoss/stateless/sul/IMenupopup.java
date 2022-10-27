/* IMenupopup.java

	Purpose:

	Description:

	History:
		Fri Oct 15 18:40:57 CST 2021, Created by katherine

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
import org.zkoss.zul.Menupopup;

/**
 * Immutable {@link Menupopup} component
 *
 * <p>
 * A container used to display menus. It should be placed inside a
 * {@link IMenu}.
 * </p>
 * @author katherine
 * @see Menupopup
 */
@StatelessStyle
public interface IMenupopup extends IPopupBase<IMenupopup>, IAnyGroup<IMenupopup>,
		IChildable<IMenupopup, IChildrenOfMenupopup> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IMenupopup DEFAULT = new IMenupopup.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Menupopup> getZKType() {
		return Menupopup.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.menu.Menupopup"}</p>
	 */
	default String getWidgetClass() {
		return "zul.menu.Menupopup";
	}

	/**
	 * Returns the instance with the given {@link IChildrenOfMenupopup} children.
	 * @param children The children of {@link IChildrenOfMenupopup}
	 */
	static IMenupopup of(IChildrenOfMenupopup... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given {@link IChildrenOfMenupopup} children.
	 * @param children The children of {@link IChildrenOfMenupopup}
	 */
	static IMenupopup of(Iterable<? extends IChildrenOfMenupopup> children) {
		return new IMenupopup.Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IMenupopup ofId(String id) {
		return new IMenupopup.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IMenupopup IMenupopup}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIMenupopup.Builder {}

	/**
	 * Builds an updater of type {@link IMenupopup} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IMenupopupUpdater {}
}