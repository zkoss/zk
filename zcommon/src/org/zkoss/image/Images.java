/* Images.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 24 15:10:53     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.image;

import java.io.IOException;
import java.awt.image.RenderedImage;

import org.zkoss.image.Image;
import org.zkoss.image.AImage;

import org.zkoss.image.encoder.ImageEncoders;
import org.zkoss.image.encoder.ImageEncoder;

/**
 * Utilities to handle images.
 *
 * @author tomyeh
 * @since 3.0.7
 */
public class Images {
	/** Encodes an AWT image into a ZK image (in PNG, JPEG or other format).
	 *
	 * @param name The name of the image. The extension of the name must
	 * be the format. Supported format: png and jpeg.
	 * For example, foo.png and foo.jpeg.
	 * @param image  The AWT image to be encoded.
     * @param quality  The quality to use for the image encoding.
	 * It is a number between 0 and 1. The higher the value,
	 * the better the output quality.
     * Not applicable to "png".
     * @param encodeAlpha  Whether to encode alpha transparency.
     * Not applicable to "png".
     * @exception IllegalArgumentException if name doesn't contain an
     * extension with supported format.
	 */
	public static Image encode(String name, RenderedImage image,
	float quality, boolean encodeAlpha)
	throws IOException {
		ImageEncoder encoder = ImageEncoders.newInstance(getFormat(name));
		encoder.setQuality(quality);
		encoder.setEncodingAlpha(encodeAlpha);
		return new AImage(name, encoder.encode(image));
	}
	/** Encodes an AWT image into a ZK image.
	 *
	 * @param name The name of the image. The extension of the name must
	 * be the format. For example, foo.png and foo.jpeg.
	 * @param image  The AWT image to be encoded.
     * @exception IllegalArgumentException if name doesn't contain an
     * extension with supported format.
	 */
	public static Image encode(String name, RenderedImage image)
	throws IOException {
		ImageEncoder encoder = ImageEncoders.newInstance(getFormat(name));
		return new AImage(name, encoder.encode(image));
	}
	private static String getFormat(String name) {
		final int j = name.lastIndexOf('.');
		if (j < 0)
			throw new IllegalArgumentException("Illegal name: "+name+"\nIt must contain the extension as the format, such as foo.png");
		return name.substring(j + 1);
	}
}
