/* JPEGEncoder.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 24 16:16:29     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.image.encoder;

import java.io.ByteArrayOutputStream;
import java.awt.image.RenderedImage;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.zkoss.lang.SystemException;

/**
 * The encoder for encoding an image into the JPEG format
 *
 * @author tomyeh
 * @since 3.0.7
 */
public class JPEGEncoder implements ImageEncoder {
	private float _quality = 0.95f;

	public JPEGEncoder() {
	}

	public byte[] encode(RenderedImage image) throws java.io.IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageWriter iw = (ImageWriter)ImageIO.getImageWritersByFormatName("jpeg").next();
		ImageWriteParam iwp = iw.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(_quality);
		ImageOutputStream ios = ImageIO.createImageOutputStream(os);
		iw.setOutput(ios);
		iw.write(null, new IIOImage(image, null, null), iwp);
		ios.flush();
		iw.dispose();
		ios.close();
		return os.toByteArray();
	}
	/** Returns the quality of the image encoding.
	 * It is a number between 0 and 1. The higher the value,
	 * the better the output quality.
	 *
	 * <p>Default: 0.95f.
	 */
	public float getQuality() {
		return _quality;
	}
	public void setQuality(float quality) {
		if (quality < 0.0f || quality > 1.0f)
			throw new IllegalArgumentException("out of range: "+quality);
		_quality = quality;
	}
	public boolean isEncodingAlpha() {
		return false;
	}
	public void setEncodingAlpha(boolean encodeAlpha) {
	}
}
