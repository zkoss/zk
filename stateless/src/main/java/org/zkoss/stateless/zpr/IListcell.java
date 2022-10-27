/* IListcell.java

	Purpose:

	Description:

	History:
		Tue Oct 19 16:45:35 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Listcell;

/**
 * Immutable {@link Listcell} component
 *
 * @author katherine
 * @see Listcell
 */
@StatelessStyle
public interface IListcell<I extends IAnyGroup> extends ILabelImageElement<IListcell<I>>,
		IChildable<IListcell<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IListcell<IAnyGroup> DEFAULT = new IListcell.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Listcell> getZKType() {
		return Listcell.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Listcell"}</p>
	 */
	default String getWidgetClass() {
		return "zul.sel.Listcell";
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
	default IListcell withWidth(@Nullable String width) {
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
	default IListcell withHflex(@Nullable String hflex) {
		throw new UnsupportedOperationException("readonly");
	}

	/** Returns number of columns to span this cell.
	 * Default: {@code 1}.
	 */
	default int getSpan() {
		return 1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code span}.
	 *
	 * <p> Sets number of columns to span this cell.
	 *
	 * @param span Number of columns to span this cell.
	 * <p>Default: {@code 1}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IListcell<I> withSpan(int span);

	/**
	 * Returns the instance with the given label.
	 * @param label The label that the cell holds.
	 */
	static <I extends IAnyGroup> IListcell<I> of(String label) {
		return new IListcell.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the cell holds.
	 * @param image The image that the cell holds.
	 */
	static <I extends IAnyGroup> IListcell<I> of(String label, String image) {
		return new IListcell.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 *
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IListcell<I> of(Iterable<? extends I> children) {
		return new IListcell.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IListcell<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the cell holds.
	 */
	static <I extends IAnyGroup> IListcell<I> ofImage(String image) {
		return new IListcell.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IListcell<I> ofId(String id) {
		return new IListcell.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		ILabelImageElement.super.renderProperties(renderer);

		int _colspan = getSpan();
		if (_colspan > 1)
			renderer.render("colspan", _colspan);
	}

	/**
	 * Builds instances of type {@link IListcell IListcell}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIListcell.Builder<I> {}

	/**
	 * Builds an updater of type {@link IListcell} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IListcellUpdater {}
}