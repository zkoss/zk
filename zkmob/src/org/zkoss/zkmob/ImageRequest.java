/* ImageLoader.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 21, 2007 5:10:52 PM, Created by henrichen
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
 * ImageLoader that load image in another thread and setup associated {@link Imageable} item.  
 * @author henrichen
 *
 */
public class ImageRequest implements Runnable {
	private Imageable _item;
	private String _imagesrc;
	
	public ImageRequest(Imageable item, String imagesrc) {
		_item = item;
		_imagesrc = imagesrc;
	}
	
	public void run() {
		Image image = UiManager.loadImage(_imagesrc);
		_item.loadImage(image);
	}
}
