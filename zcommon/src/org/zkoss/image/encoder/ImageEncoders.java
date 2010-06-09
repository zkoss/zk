/* ImageEncoders.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 24 16:08:13     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.image.encoder;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.awt.image.RenderedImage;

import org.zkoss.lang.SystemException;
import org.zkoss.image.Image;
import org.zkoss.image.AImage;

/**
 * The factory of the image encodes.
 *
 * @author tomyeh
 * @since 3.0.7
 */
public class ImageEncoders {
	/** Map(String format, Class encoder). */
	private final static Map _encoders;

	static {
		final Map encoders = new HashMap(4);
		encoders.put("png", PNGEncoder.class);
		encoders.put("jpeg", JPEGEncoder.class);
		_encoders = Collections.synchronizedMap(encoders);
	}

	/** Instantiates and returns the image encoder for the specified format.
	 *
     * @param format  The image format of the ZK image being encoded to.
     * It can be "png" or "jpeg".
     * @exception IllegalArgumentException if the encoder of the specified
     * format is not found
     * @exception SystemException if failed to instantiate the encoder
     */
    public static ImageEncoder newInstance(String format) {
    	final Class klass = (Class)_encoders.get(format.toLowerCase());
    	if (klass == null)
    		throw new IllegalArgumentException("Unsupported format: "+format);

    	try {
    		return (ImageEncoder)klass.newInstance();
    	} catch (Throwable ex) {
    		throw SystemException.Aide.wrap(ex);
    	}
    }
    /** Sets the class of the image encoder for the specified format.
     *
     * <p>An instance of the specified class is instantiated each
     * time {@link #newInstance} is called.
     *
     * @param format  The image format of the ZK image being encoded to.
     * It can be "png" or "jpeg".
     * @param klass the class of the image encoder.
     * It must implement {@link ImageEncoder}.
     * @return the previous encoder, or null if not set yet.
     */
    public static Class setEncoderClass(String format, Class klass) {
    	if (!ImageEncoder.class.isAssignableFrom(klass))
    		throw new IllegalArgumentException(ImageEncoder.class+" must be implemented by "+klass);
    	return (Class)_encoders.put(format.toLowerCase(), klass);
    }
    /** Returns the class of the image encoder for the specified format,
     * or null if not specified yet.
     */
    public static Class getEncoderClass(String format) {
    	return (Class)_encoders.get(format.toLowerCase());
    }
}
