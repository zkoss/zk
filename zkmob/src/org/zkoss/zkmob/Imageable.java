/* Imageable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 21, 2007 4:26:53 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import javax.microedition.lcdui.Image;

/**
 * A JavaMe component with setImage() method; to be used with 
 * {@link ImageRequest}.
 * 
 * @author henrichen
 */
public interface Imageable {
	/** Setup {@link Image}.
	 * @param image The Image.
	 */
	public void loadImage(Image image);
}
