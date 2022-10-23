/* ITabpanels.java

	Purpose:

	Description:

	History:
		Thu Oct 28 14:57:43 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zul.Tabpanels;

/**
 * Immutable {@link Tabpanels} component
 *
 * <p>A tabpanels is the container for the tab panels, i.e., a collection of tabpanel components.</p>
 * @author katherine
 * @see Tabpanels
 */
@ZephyrStyle
public interface ITabpanels extends IXulElement<ITabpanels>, IChildable<ITabpanels, ITabpanel>,
		ITabboxComposite<ITabpanels> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITabpanels DEFAULT = new ITabpanels.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Tabpanels> getZKType() {
		return Tabpanels.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.tab.Tabpanels"}</p>
	 */
	default String getWidgetClass() {
		return "zul.tab.Tabpanels";
	}

	/**
	 *
	 * Returns the instance with the given {@link ITabpanel} children.
	 * @param children The children of {@link ITabpanel}
	 */
	static ITabpanels of(Iterable<? extends ITabpanel> children) {
		return new ITabpanels.Builder().setChildren(children).build();
	}

	/**
	 *
	 * Returns the instance with the given {@link ITabpanel} children.
	 * @param children The children of {@link ITabpanel}
	 */
	static ITabpanels of(ITabpanel... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ITabpanels ofId(String id) {
		return new ITabpanels.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link ITabpanels ITabpanels}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITabpanels.Builder {
	}

	/**
	 * Builds an updater of type {@link ITabpanels} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ITabpanelsUpdater {}
}