/* ITreefoot.java

	Purpose:

	Description:

	History:
		5:24 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zul.Treefoot;

/**
 * Immutable {@link Treefoot} component
 * @author jumperchen
 * @see org.zkoss.zul.Treefoot
 */
@ZephyrStyle
public interface ITreefoot extends IXulElement<ITreefoot>,
		IChildable<ITreefoot, ITreefooter<IAnyGroup>>, ITreeComposite<ITreefoot> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITreefoot DEFAULT = new ITreefoot.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Treefoot> getZKType() {
		return Treefoot.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Treefoot"}</p>
	 */
	default String getWidgetClass() {
		return "zul.sel.Treefoot";
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
	 * components, please refer to {@link ITree} and {@link ITreecol} instead.
	 */
	default ITreefoot withWidth(@Nullable String width) {
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
	 * components, please refer to {@link ITree} and {@link ITreecol} instead.
	 */
	default ITreefoot withHflex(@Nullable String hflex) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Returns the instance with the given tree footers which belong to this component.
	 * @param children The tree footers of the component.
	 */
	static ITreefoot of(Iterable<? extends ITreefooter<IAnyGroup>> children) {
		return new Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given tree footers which belong to this component.
	 * @param children The tree footers of the component.
	 */
	static ITreefoot of(ITreefooter<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ITreefoot ofId(String id) {
		return new ITreefoot.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link ITreefoot ITreefoot}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITreefoot.Builder {}

	/**
	 * Builds an updater of type {@link ITreefoot} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ITreefootUpdater {}
}
