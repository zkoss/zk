/* ILabelImageElement.java

	Purpose:

	Description:

	History:
		Tue Oct 05 15:09:42 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import javax.annotation.Nullable;

import org.zkoss.lang.Library;
import org.zkoss.stateless.immutable.StatelessOnly;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * Immutable {@link LabelImageElement} interface
 *
 * <p>To turn on the preload image facility for this component, you have to specify
 * {@link #withPreloadImage(boolean)} to true.
 *
 * <p>Or configure it from zk.xml by setting library properties.
 * For example,
 * <pre>
 * <code> &lt;library-property/&gt;
 *     &lt;name&gt;org.zkoss.zul.image.preload&lt;/name/&gt;
 *     &lt;value&gt;true&lt;/value/&gt;
 * &lt;/library-property/&gt;
 * </code>
 * </pre>
 * <b>Note:</b> with zk.xml setting, it will affect to all subcomponents, which are
 * extended from {@link ILabelImageElement}
 * @author katherine
 */
public interface ILabelImageElement<I extends ILabelImageElement>
		extends ILabelElement<I>, IChildrenOfInputgroup<I> {

	/**
	 * Returns the icon font CSS class name.
	 */
	@Nullable
	default String getIconSclass() {
		return null;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code iconSclass}.
	 *
	 * <p>Sets the CSS class name for the icon font.
	 * @param iconSclass A CSS class name for the icon font.
	 * @return A modified copy of the {@code this} object
	 */
	I withIconSclass(@Nullable String iconSclass);

	/**
	 * Returns the image URI.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	default String getImage() {
		return null;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code image} URI.
	 *
	 * <p>Sets the image URI.
	 * The image would hide if specify to {@code null} </p>
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withImage(@Nullable String image);

	/**
	 * Returns the URI of the hover image.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	default String getHoverImage() {
		return null;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code hoverImage} URI.
	 *
	 * <p>Sets the image URI.
	 * The hover image is used when the mouse is moving over this component.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withHoverImage(@Nullable String hoverImage);

	/**
	 * Returns whether to preload the image.
	 * <p>Default: {@code false}, if the {@code "org.zkoss.zul.image.preload"}
	 * library property is not set in zk.xml.</p>
	 */
	@StatelessOnly
	default boolean isPreloadImage() {
		return Boolean.parseBoolean(Library.getProperty("org.zkoss.zul.image.preload", "false"));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code preloadImage} URI.
	 *
	 * <p>Sets to {@code true} to enable to preload the image.
	 *
	 * <b>Note:</b> the priority of this attribute is higher than zk.xml if both
	 * preload image are specified.
	 *
	 * @return A modified copy of the {@code this} object
	 */
	I withPreloadImage(boolean preloadImage);

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		ILabelElement.super.renderProperties(renderer);
		//ZK-1638: preload image can also be defined in zk.xml by library property
		render(renderer, "_preloadImage", isPreloadImage());
		render(renderer, "image", Executions.getCurrent().encodeURL(getImage()));
		render(renderer, "hoverImage", Executions.getCurrent().encodeURL(getHoverImage()));
		render(renderer, "iconSclass", getIconSclass());
	}
}
