/* IImage.java

	Purpose:

	Description:

	History:
		Tue Oct 26 10:47:33 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.awt.image.RenderedImage;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Image;

/**
 * Immutable {@link Image} component
 *
 * <p>An IImage component is used to display an image at the browser. There are two ways to assign an image to an image component.
 * First, you could use the {@link #of(String)} or {@link #withSrc(String)} to specify a URI where the image is located. This approach is similar to what HTML supports.
 * It is useful if you want to display a static image, or any image that can be identified by URL.
 * For example:
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/example")
 * public IComponent example() {
 *     return IImage.of("/stateless/ZK-Logo.gif");
 * }
 * </code>
 * </pre>
 *
 * <p>Secondly, you could use the {@link #withContent(org.zkoss.image.Image)} or {@link #withContent(RenderedImage)} method to set the content of an image to an image component directly.
 *
 * @author katherine
 * @see Image
 */
@StatelessStyle
public interface IImage extends IImageBase<IImage>, IAnyGroup<IImage> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IImage DEFAULT = new Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Image> getZKType() {
		return Image.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Image"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Image";
	}

	/**
	 * Returns the instance with the given {@code src}.
	 * @param src The URI of the image source.
	 */
	static IImage of(String src) {
		return new IImage.Builder().setSrc(src).build();
	}

	/**
	 * Returns the instance with the given {@code width} and {@code height}.
	 * @param width The width of the image.
	 * @param height The height of the image.
	 */
	static IImagemap ofSize(String width, String height) {
		return new IImagemap.Builder().setWidth(width).setHeight(height).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IImage ofId(String id) {
		return new IImage.Builder().setId(id).build();
	}

	/**
	 * Builds instances of type {@link IImage IImage}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIImage.Builder {
	}

	/**
	 * Builds an updater of type {@link IImage} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IImageUpdater {}
}