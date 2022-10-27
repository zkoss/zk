/* ITreecell.java

	Purpose:

	Description:

	History:
		4:04 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Treecell;

/**
 * Immutable {@link Treecell} component
 * <p>ITreecell represents one column in a treerow by sequential.
 * Treecell can contain any components in it, such as label, image, textbox etc..</p>
 * @author jumperchen
 * @see Treecell
 */
@StatelessStyle
public interface ITreecell<I extends IAnyGroup>
		extends ILabelImageElement<ITreecell<I>>, IChildable<ITreecell<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITreecell<IAnyGroup> DEFAULT = new Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Treecell> getZKType() {
		return Treecell.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Treecell"}</p>
	 */
	default String getWidgetClass() {
		return "zul.sel.Treecell";
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
	 * To control the size of Treecell related
	 * components, please refer to {@link ITree} and {@link ITreecol} instead.
	 */
	default ITreecell<I> withWidth(@Nullable String width) {
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
	 * To control the size of Treecell related
	 * components, please refer to {@link ITree} and {@link ITreecol} instead.
	 */
	default ITreecell<I> withHflex(@Nullable String hflex) {
		throw new UnsupportedOperationException("readonly");
	}

	/** Returns number of columns to span this cell.
	 * <p>Default: {@code 1}.
	 */
	default int getSpan() {
		return 1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code span}.
	 *
	 * <p>Sets the number of columns to span this cell.
	 * It is the same as the colspan attribute of HTML TD tag.
	 *
	 * @param span The number of columns to span this cell.
	 * <p>Default: {@code 1}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITreecell<I> withSpan(int span);

	/**
	 * Returns the instance with the given label.
	 * @param label The label that the cell holds.
	 */
	static <I extends IAnyGroup> ITreecell<I> of(String label) {
		return new Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the cell holds.
	 * @param image The image that the cell holds.
	 */
	static <I extends IAnyGroup> ITreecell<I> of(String label, String image) {
		return new Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> ITreecell<I> ofId(String id) {
		return new Builder().setId(id).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the cell holds.
	 */
	static <I extends IAnyGroup> ITreecell<I> ofImage(String image) {
		return new Builder().setImage(image).build();
	}

	/**
	 *
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ITreecell<I> of(Iterable<? extends I> children) {
		return new Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ITreecell<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		ILabelImageElement.super.renderProperties(renderer);

		if (getSpan() > 1)
			renderer.render("colspan", getSpan());
	}

	/**
	 * Builds instances of type {@link ITreecell ITreecell}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableITreecell.Builder<I> {}

	/**
	 * Builds an updater of type {@link ITreecell} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ITreecellUpdater {}
}
