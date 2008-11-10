package org.zkoss.zul.impl.api;

import java.awt.image.RenderedImage;
import org.zkoss.image.Image;
import org.zkoss.image.Images;

/**
 * A XUL element with a label ({@link #getLabel}) and an image (
 * {@link #getImage}).
 * 
 * @author tomyeh
 */
public interface LabelImageElement extends LabelElement {
	/**
	 * Returns the image URI.
	 * <p>
	 * Default: null.
	 */
	public String getImage();

	/**
	 * Sets the image URI.
	 * <p>
	 * Calling this method implies setImageContent(null). In other words, the
	 * last invocation of {@link #setImage} overrides the previous
	 * {@link #setImageContent}, if any.
	 * <p>
	 * If src is changed, the component's inner is invalidate. Thus, you want to
	 * smart-update, you have to override this method.
	 * 
	 * @see #setImageContent(Image)
	 * @see #setImageContent(RenderedImage)
	 */
	public void setImage(String src);

	/**
	 * @deprecated As of release 3.5.0, it is redudant since it is the same as
	 *             {@link #getImage}
	 */
	public String getSrc();

	/**
	 * @deprecated As of release 3.5.0, it is redudant since it is the same as
	 *             {@link #setImage}
	 */
	public void setSrc(String src);

	/**
	 * Sets the content directly.
	 * <p>
	 * Default: null.
	 * 
	 * <p>
	 * Calling this method implies setImage(null). In other words, the last
	 * invocation of {@link #setImageContent} overrides the previous
	 * {@link #setImage}, if any.
	 * 
	 * @param image
	 *            the image to display.
	 * @see #setImage
	 */
	public void setImageContent(Image image);

	/**
	 * Sets the content directly with the rendered image. It actually encodes
	 * the rendered image to an PNG image ({@link org.zkoss.image.Image}) with
	 * {@link Images#encode}, and then invoke
	 * {@link #setImageContent(org.zkoss.image.Image)}.
	 * 
	 * <p>
	 * If you want more control such as different format, quality, and naming,
	 * you can use {@link Images} directly.
	 * 
	 * @since 3.0.7
	 */
	public void setImageContent(RenderedImage image);

	/**
	 * Returns the image content set by {@link #setImageContent(Image)} or
	 * {@link #setImageContent(RenderedImage)}.
	 * 
	 * <p>
	 * Note: it won't load the content specified by {@link #setImage}. Actually,
	 * it returns null if {@link #setImage} was called.
	 */
	public Image getImageContent();

	/**
	 * Returns the URI of the hover image. The hover image is used when the
	 * mouse is moving over this component.
	 * <p>
	 * Default: null.
	 * 
	 * @since 3.5.0
	 */
	public String getHoverImage();

	/**
	 * Sets the image URI. The hover image is used when the mouse is moving over
	 * this component.
	 * <p>
	 * Calling this method implies setHoverImageContent(null). In other words,
	 * the last invocation of {@link #setHoverImage} overrides the previous
	 * {@link #setHoverImageContent}, if any.
	 * 
	 * @since 3.5.0
	 */
	public void setHoverImage(String src);

	/**
	 * Sets the content of the hover image directly. The hover image is used
	 * when the mouse is moving over this component.
	 * <p>
	 * Default: null.
	 * 
	 * <p>
	 * Calling this method implies setHoverImage(null). In other words, the last
	 * invocation of {@link #setHoverImageContent} overrides the previous
	 * {@link #setHoverImage}, if any.
	 * 
	 * @param image
	 *            the image to display.
	 * @since 3.5.0
	 */
	public void setHoverImageContent(Image image);

	/**
	 * Sets the content of the hover image directly with the rendered image. The
	 * hover image is used when the mouse is moving over this component.
	 * 
	 * <p>
	 * It actually encodes the rendered image to an PNG image (
	 * {@link org.zkoss.image.Image}) with {@link Images#encode}, and then
	 * invoke {@link #setHoverImageContent(org.zkoss.image.Image)}.
	 * 
	 * <p>
	 * If you want more control such as different format, quality, and naming,
	 * you can use {@link Images} directly.
	 * 
	 * @since 3.5.0
	 */
	public void setHoverImageContent(RenderedImage image);

	/**
	 * Sets the content of the hover image directly with the rendered image. The
	 * hover image is used when the mouse is moving over this component.
	 * 
	 * <p>
	 * It actually encodes the rendered image to an PNG image (
	 * {@link org.zkoss.image.Image}) with {@link Images#encode}, and then
	 * invoke {@link #setHoverImageContent(org.zkoss.image.Image)}.
	 * 
	 * <p>
	 * If you want more control such as different format, quality, and naming,
	 * you can use {@link Images} directly.
	 * 
	 * @since 3.5.0
	 */
	public boolean isImageAssigned();

	/**
	 * Returns the HTML IMG tag for the image part, or null if no image is
	 * assigned ({@link #isImageAssigned})
	 * 
	 * <p>
	 * Used only for component development, not for application developers.
	 * 
	 * <p>
	 * Note: the component template shall use this method to generate the HTML
	 * tag, instead of using {@link #getImage}.
	 */
	public String getImgTag();

	/**
	 * Returns the encoded URL for the image ({@link #getImage} or
	 * {@link #getImageContent}), or null if no image.
	 * <p>
	 * Used only for component developements; not by app developers.
	 * <p>
	 * Note: this method can be invoked only if execution is not null.
	 * 
	 * @since 3.5.0
	 */
	public String getEncodedImageURL();
}
