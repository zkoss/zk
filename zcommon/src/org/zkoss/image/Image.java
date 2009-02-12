/* Image.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct  7 22:01:19     2004, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.image;

import javax.swing.ImageIcon;

import org.zkoss.util.media.Media;

/**
 * Represents an image.
 *
 * <p>To encode AWT image to an instance of {@link Image},
 * use {@link Images#encode}.
 *
 * @author tomyeh
 * @see Images#encode
 */
public interface Image extends Media {
	/** Returns the width.
	 */
	public int getWidth();
	/** Returns the height.
	 */
	public int getHeight();
	/** Converts to an image icon.
	 */
	public ImageIcon toImageIcon();
}
