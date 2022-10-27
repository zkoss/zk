/* ITabpanel.java

	Purpose:

	Description:

	History:
		Thu Oct 28 15:02:09 CST 2021, Created by katherine

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
import org.zkoss.zul.Tabpanel;

/**
 * Immutable {@link Tabpanel} component
 *
 * <p>A tabpanel is the body of a single tab panel.
 * You would place the content for a group of components within a tab panel.
 * The first tabpanel corresponds to the first tab, the second tabpanel corresponds
 * to the second tab and so on.</p>
 *
 * @author katherine
 * @see Tabpanel
 */
@StatelessStyle
public interface ITabpanel<I extends IAnyGroup> extends IXulElement<ITabpanel<I>>, IChildable<ITabpanel<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITabpanel<IAnyGroup> DEFAULT = new ITabpanel.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Tabpanel> getZKType() {
		return Tabpanel.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.tab.Tabpanel"}</p>
	 */
	default String getWidgetClass() {
		return "zul.tab.Tabpanel";
	}

	/**
	 *
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ITabpanel<I> of(Iterable<? extends I> children) {
		return new ITabpanel.Builder<I>().setChildren(children).build();
	}

	/**
	 *
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ITabpanel<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> ITabpanel<I> ofId(String id) {
		return new ITabpanel.Builder<I>().setId(id).build();
	}

	/**
	 * Builds instances of type {@link ITabpanel ITabpanel}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableITabpanel.Builder<I> {
	}

	/**
	 * Builds an updater of type {@link ITabpanel} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ITabpanelUpdater {}
}