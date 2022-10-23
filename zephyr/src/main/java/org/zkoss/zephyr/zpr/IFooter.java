/* IFooter.java

	Purpose:

	Description:

	History:
		Tue Dec 28 15:03:01 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Footer;

/**
 * Immutable {@link Footer} component
 *
 * @author katherine
 * @see Footer
 */
@ZephyrStyle
public interface IFooter<I extends IAnyGroup>
		extends IFooterElement<IFooter<I>>, IChildable<IFooter<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IFooter<IAnyGroup> DEFAULT = new IFooter.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Footer> getZKType() {
		return Footer.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.grid.Footer"}</p>
	 */
	default String getWidgetClass() {
		return "zul.grid.Footer";
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IFooter<I> of(Iterable<? extends I> children) {
		return new IFooter.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IFooter<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given label.
	 * @param label The label that the component
	 */
	static <I extends IAnyGroup> IFooter<I> of(String label) {
		return new IFooter.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the component holds.
	 * @param image The image that the component holds.
	 */
	static <I extends IAnyGroup> IFooter<I> of(String label, String image) {
		return new IFooter.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the component holds.
	 */
	static <I extends IAnyGroup> IFooter<I> ofImage(String image) {
		return new IFooter.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IFooter<I> ofId(String id) {
		return new IFooter.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IFooterElement.super.renderProperties(renderer);
		org.zkoss.zul.impl.Utils.renderCrawlableText(getLabel());
	}

	/**
	 * Builds instances of type {@link IFooter IFooter}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIFooter.Builder<I> {
	}

	/**
	 * Builds an updater of type {@link IFooter} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IFooterUpdater {}
}