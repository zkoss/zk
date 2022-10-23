/* ITreecols.java

	Purpose:

	Description:

	History:
		4:47 PM 2021/10/25, Created by jumperchen

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
import org.zkoss.zul.Treecols;

/**
 * Immutable {@link Treecols} component
 * @author jumperchen
 * @see Treecols
 */
@ZephyrStyle
public interface ITreecols extends IHeadersElement<ITreecols>,
		IChildable<ITreecols, ITreecol>, ITreeComposite<ITreecols> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITreecols DEFAULT = new ITreecols.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Treecols> getZKType() {
		return Treecols.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Treecols"}</p>
	 */
	default String getWidgetClass() {
		return "zul.sel.Treecols";
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
	default ITreecols withWidth(@Nullable String width) {
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
	default ITreecols withHflex(@Nullable String hflex) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Returns the instance with the given tree columns which belong to this component.
	 * @param children The tree columns of the component.
	 */
	static ITreecols of(Iterable<? extends ITreecol<IAnyGroup>> children) {
		return new Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given tree columns which belong to this component.
	 * @param children The tree columns of the component.
	 */
	static ITreecols of(ITreecol<IAnyGroup>... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static  ITreecols ofId(String id) {
		return new ITreecols.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link ITreecols ITreecols}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITreecols.Builder {}

	/**
	 * Builds an updater of type {@link ITreecols} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ITreecolsUpdater {}
}
