/* ITreefooter.java

	Purpose:

	Description:

	History:
		5:27 PM 2021/10/25, Created by jumperchen

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
import org.zkoss.zul.Treefooter;

/**
 * Immutable {@link org.zkoss.zul.Treefooter} component
 * <p>A treefooter is a bottom column of tree, Its parent must be Treefoot.
 * You could place any child in a tree footer.</p>
 * @author jumperchen
 * @see Treefooter
 */
@StatelessStyle
public interface ITreefooter<I extends IAnyGroup>
		extends IFooterElement<ITreefooter<I>>, IChildable<ITreefooter<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITreefooter<IAnyGroup> DEFAULT = new ITreefooter.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Treefooter> getZKType() {
		return Treefooter.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Treefooter"}</p>
	 */
	default String getWidgetClass() {
		return "zul.sel.Treefooter";
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ITreefooter<I> of(Iterable<? extends I> children) {
		return new Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ITreefooter<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given label.
	 * @param label The label belongs to this footer.
	 */
	static <I extends IAnyGroup> ITreefooter<I> of(String label) {
		return new Builder<I>().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the footer holds.
	 * @param image The image that the footer holds.
	 */
	static <I extends IAnyGroup> ITreefooter<I> of(String label, String image) {
		return new Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the footer holds.
	 */
	static <I extends IAnyGroup> ITreefooter<I> ofImage(String image) {
		return new Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> ITreefooter<I> ofId(String id) {
		return new Builder<I>().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	//-- super --//
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		IFooterElement.super.renderProperties(renderer);
		org.zkoss.zul.impl.Utils.renderCrawlableText(getLabel());
	}

	/**
	 * Builds instances of type {@link ITreefooter ITreefooter}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableITreefooter.Builder<I> {}

	/**
	 * Builds an updater of type {@link ITreefooter} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ITreefooterUpdater {}
}
