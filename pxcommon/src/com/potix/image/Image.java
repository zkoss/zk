/* Image.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct  7 22:01:19     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.image;

import javax.swing.ImageIcon;

import com.potix.util.media.Media;

/**
 * Represents an image.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.9 $ $Date: 2006/05/29 04:27:18 $
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
