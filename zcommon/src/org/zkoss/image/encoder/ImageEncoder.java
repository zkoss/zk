/* ImageEncoder.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 24 15:44:41     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.image.encoder;

import java.awt.image.RenderedImage;

/**
 * Represents an image encoder used to encode an AWT image into
 * a particular format, such as PNG.
 *
 * @author tomyeh
 * @see ImageEncoders#newInstance
 * @see ImageEncoders#setEncoderClass
 * @since 3.0.7
 */
public interface ImageEncoder {
	/** Encodes an AWT image into a byte array in a particular format.
	 */
	public byte[] encode(RenderedImage image) throws java.io.IOException;
	/** Return the quality of the image encoding.
	 */
	public float getQuality();
	/** Sets the quality of the image encoding.
	 * Simply does nothing if the encoder does not support it.
	 */
	public void setQuality(float quality);
	/** Returns whether to encode the alpha transparency.
	 */
	public boolean isEncodingAlpha();
	/** Sets whether to encode the alpha transparency.
	 * Simply does nothing if the encoder does not support it.
	 */
	public void setEncodingAlpha(boolean encodeAlpha);
}
