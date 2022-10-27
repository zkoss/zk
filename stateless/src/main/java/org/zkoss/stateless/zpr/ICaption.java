/* ICaption.java

	Purpose:

	Description:

	History:
		Tue Oct 19 15:45:55 CST 2021, Created by katherine

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
import org.zkoss.zul.Caption;

/**
 * Immutable {@link Caption} component.
 *
 * <p>A header for a {@link IGroupbox}, {@link IWindow}, and {@link IPanel}.
 * It may contain either a text label, using {@link #withLabel},
 * or child elements for a more complex caption.</p>
 *
 * @author katherine
 * @see Caption
 */
@StatelessStyle
public interface ICaption<I extends IAnyGroup>  extends ILabelImageElement<ICaption<I>>,
		IChildrenOfPanel<ICaption<I>>, IChildable<ICaption<I>, I>, IAnyGroup<ICaption<I>> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ICaption<IAnyGroup> DEFAULT = new ICaption.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Caption> getZKType() {
		return Caption.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Caption"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Caption";
	}

	/**
	 * Returns the instance with the given label
	 * @param label The label of the caption
	 */
	static <I extends IAnyGroup> ICaption<I> of(String label) {
		return new ICaption.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image
	 * @param label The label of the caption
	 * @param image The image of the caption
	 */
	static <I extends IAnyGroup> ICaption<I> of(String label, String image) {
		return new ICaption.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 *
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ICaption<I> of(Iterable<? extends I> children) {
		return new ICaption.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ICaption<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given image
	 * @param image The image of the caption
	 */
	static <I extends IAnyGroup> ICaption<I> ofImage(String image) {
		return new ICaption.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> ICaption<I> ofId(String id) {
		return new ICaption.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link ICaption ICaption}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableICaption.Builder<I> {}

	/**
	 * Builds an updater of type {@link ICaption} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ICaptionUpdater {}
}