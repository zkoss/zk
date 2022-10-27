/* IListfooter.java

	Purpose:

	Description:

	History:
		Tue Jan 04 14:16:46 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Listfooter;

/**
 * Immutable {@link Listfooter} component
 *
 * @author katherine
 * @see Listfooter
 */
@StatelessStyle
public interface IListfooter<I extends IAnyGroup> extends IFooterElement<IListfooter<I>>,
		IChildable<IListfooter<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IListfooter<IAnyGroup> DEFAULT = new IListfooter.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Listfooter> getZKType() {
		return Listfooter.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Listfooter"}</p>
	 * @return
	 */
	default String getWidgetClass() {
		return "zul.sel.Listfooter";
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IListfooter<I> of(Iterable<? extends I> children) {
		return new IListfooter.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IListfooter<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}
	/**
	 * Returns the instance with the given label.
	 * @param label The label belongs to this footer.
	 */
	static <I extends IAnyGroup> IListfooter<I> of(String label) {
		return new IListfooter.Builder<I>().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the footer holds.
	 * @param image The image that the footer holds.
	 */
	static <I extends IAnyGroup> IListfooter<I> of(String label, String image) {
		return new IListfooter.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the footer holds.
	 */
	static <I extends IAnyGroup> IListfooter<I> ofImage(String image) {
		return new IListfooter.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IListfooter<I> ofId(String id) {
		return new IListfooter.Builder<I>().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IFooterElement.super.renderProperties(renderer);
		org.zkoss.zul.impl.Utils.renderCrawlableText(getLabel());
	}

	/**
	 * Builds instances of type {@link IListfooter IListfooter}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIListfooter.Builder<I> {
	}

	/**
	 * Builds an updater of type {@link IListfooter} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IListfooterUpdater {}
}