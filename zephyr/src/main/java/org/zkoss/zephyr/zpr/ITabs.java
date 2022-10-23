/* ITabs.java

	Purpose:

	Description:

	History:
		Thu Oct 28 09:48:13 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zul.Tabs;

/**
 * Immutable {@link Tabs} component
 * <p>A collection of tabs ({@link ITab}).</p>
 *
 * @author katherine
 * @see Tabs
 */
@ZephyrStyle
public interface ITabs extends IXulElement<ITabs>, IChildable<ITabs, ITab>,
		ITabboxComposite<ITabs> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITabs DEFAULT = new ITabs.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Tabs> getZKType() {
		return Tabs.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.tab.Tabs"}</p>
	 */
	default String getWidgetClass() {
		return "zul.tab.Tabs";
	}

	/**
	 *
	 * Returns the instance with the given {@link ITab} children.
	 * @param children The children of {@link ITab}
	 */
	static ITabs of(Iterable<? extends ITab> children) {
		return new ITabs.Builder().setChildren(children).build();
	}

	/**
	 *
	 * Returns the instance with the given {@link ITab} children.
	 * @param children The children of {@link ITab}
	 */
	static ITabs of(ITab... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 *
	 * Returns the instance with the given width.
	 * @param width The width of the component
	 */
	static ITabs ofWidth(String width) {
		return new Builder().setWidth(width).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ITabs ofId(String id) {
		return new ITabs.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link ITabs ITabs}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITabs.Builder {}

	/**
	 * Builds an updater of type {@link ITabs} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ITabsUpdater {}
}