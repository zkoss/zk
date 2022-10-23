/* IListfoot.java

	Purpose:

	Description:

	History:
		Tue Jan 04 14:16:12 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zul.Listfoot;

/**
 * Immutable {@link Listfoot} component
 *
 * @author katherine
 * @see Listfoot
 */
@ZephyrStyle
public interface IListfoot extends IListboxComposite<IListfoot>,
		IChildable<IListfoot, IListfooter>, IXulElement<IListfoot> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IListfoot DEFAULT = new IListfoot.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Listfoot> getZKType() {
		return Listfoot.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Listfoot"}</p>
	 * @return
	 */
	default String getWidgetClass() {
		return "zul.sel.Listfoot";
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
	 * components, please refer to {@link IListbox} and {@link IListheader} instead.
	 */
	default IListfoot withWidth(@Nullable String width) {
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
	 * components, please refer to {@link IListbox} and {@link IListheader} instead.
	 */
	default IListfoot withHflex(@Nullable String hflex) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Returns the instance with the given footers which belong to this component.
	 * @param children The tree footers of the component.
	 */
	static IListfoot of(Iterable<? extends IListfooter<IAnyGroup>> children) {
		return new IListfoot.Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given footers which belong to this component.
	 * @param children The tree footers of the component.
	 */
	static IListfoot of(IListfooter<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IListfoot ofId(String id) {
		return new IListfoot.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IListfoot IListfoot}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIListfoot.Builder {
	}

	/**
	 * Builds an updater of type {@link IListfoot} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IListfootUpdater {}
}