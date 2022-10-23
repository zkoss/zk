/* IImageBase.java

	Purpose:

	Description:

	History:
		Tue Oct 26 12:32:59 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.annotation.Nullable;

import org.zkoss.image.Images;
import org.zkoss.lang.Library;
import org.zkoss.zephyr.immutable.ZephyrOnly;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Image;

/**
 * Immutable {@link Image} base component.
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
 * extended from {@link IImageBase}
 * @author katherine
 */

public interface IImageBase<I extends IImageBase> extends IXulElement<I> {

	/**
	 * Returns the URI of the hover image.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getHover();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code hover}.
	 *
	 * <p>Sets the image URI.
	 * The hover image is used when the mouse is moving over this component.
	 * @param hover The hover image URI.
	 * @return A modified copy of {@code this} object
	 */
	I withHover(@Nullable String hover);

	/**
	 * Returns the content set by {@link #withContent(org.zkoss.image.Image)}.
	 * <p>Note: it won't fetch what is set through by {@link #withSrc(String)}.
	 * It simply returns what is passed to {@link #withContent(org.zkoss.image.Image)}.
	 */
	@Nullable
	org.zkoss.image.Image getContent();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code image}.
	 *
	 * <p>Sets the content directly.
	 * <p>Default: {@code null}.
	 *
	 * <p>
	 * <b>Note:</b> If calling this with {@link #withSrc(String)},
	 * the {@link #withContent(org.zkoss.image.Image)}
	 * has higher priority
	 *
	 * @param image The image to display.
	 * @return A modified copy of {@code this} object
	 * @see #withSrc(String)
	 */
	I withContent(@Nullable org.zkoss.image.Image image);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code image}.
	 *
	 * <p>Sets the content directly with the rendered image.
	 * It actually encodes the rendered image to an PNG image
	 * ({@link org.zkoss.image.Image}) with {@link Images#encode},
	 * and then invoke {@link #withContent(org.zkoss.image.Image)}}.
	 *
	 * <p>If you want more control such as different format, quality,
	 * and naming, you can use {@link Images} directly.
	 * @param image The image to display.
	 * @return A modified copy of {@code this} object
	 */
	default I withContent(@Nullable RenderedImage image) {
		try {
			return withContent(image == null ? null : Images.encode("a.png", image));
		} catch (java.io.IOException ex) {
			throw new UiException(ex);
		}
	}

	/**
	 * Returns the source URI of the image.
	 * <p>Default: {@code null}.
	 */
	@Nullable
	String getSrc();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code src}.
	 *
	 * <p>Sets the source URI of the image.
	 *
	 * <p>Notice that once the {@link #withContent} is specified, this attribute will be ignored.
	 *
	 * <h5>Locale Dependent Image</h5>
	 * <p>Like using any other properties that accept an URI, you can specify "*" for identifying a Locale dependent image.
	 * For example, if you have different images for different Locales, you could use the following code.
	 * <pre>
	 * <code>{@literal @}{@code RichletMapping}("/locale")
	 * public IComponent locale() {
	 *     return IImage.of("/zephyr/ZK-Logo*.gif");
	 * }
	 * </code>
	 * </pre>
	 * Assuming one of your users is visiting your page with de_DE as the preferred Locale.
	 * ZK will try to locate the image file called /zephyr/ZK-Logo_de_DE.gif. If it is not found, it will try /zephyr/ZK-Logo_de.gif and finally /zephyr/ZK-Logo.gif.
	 *
	 * <p>
	 * <b>Note:</b> If calling this with {@link #withContent(org.zkoss.image.Image)},
	 * the {@link #withContent(org.zkoss.image.Image)}
	 * has higher priority
	 *
	 * @param src The URI of the image source
	 * @return A modified copy of {@code this} object
	 * @see #withContent(org.zkoss.image.Image)
	 * @see #withContent(RenderedImage)
	 */
	I withSrc(@Nullable String src);

	/**
	 * Returns whether to preload the image.
	 * <p>Default: {@code false}, if the {@code "org.zkoss.zul.image.preload"}
	 * library property is not set in zk.xml.</p>
	 */
	@ZephyrOnly
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
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);
		//ZK-1638: preload image can also defined in zk.xml by library property
		render(renderer, "_preloadImage", isPreloadImage());
		render(renderer, "src", IImageCtrl.getEncodedSrc(this));

		String _hoversrc = getHover();
		if (_hoversrc != null)
			render(renderer, "hover", Executions.getCurrent().encodeURL(_hoversrc));
	}
}