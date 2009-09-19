/* PNGEncoder.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 24 16:16:24     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.image.encoder;

import java.io.ByteArrayOutputStream;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;

/**
 * The encoder for encoding an image into the PNG format
 *
 * @author tomyeh
 * @since 3.0.7
 */
public class PNGEncoder implements ImageEncoder {
	public PNGEncoder() {
	}

	public byte[] encode(RenderedImage image) throws java.io.IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
		return os.toByteArray();
	}
	/** Always return 1.0 since no quality degrade.
	 */
	public float getQuality() {
		return 1.0f;
	}
	public void setQuality(float quality) {
	}
	public boolean isEncodingAlpha() {
		return false;
	}
	public void setEncodingAlpha(boolean encodeAlpha) {
	}
}
