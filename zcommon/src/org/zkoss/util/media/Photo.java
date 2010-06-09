/* Photo.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun  8 10:48:36     2004, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.media;

import org.zkoss.image.Image;
import org.zkoss.util.ModificationException;

/**
 * Represents an object is associated with images.
 *
 * @author tomyeh
 */
public interface Photo {
	/** Returns the photo of this object.
	 */
	public Image getPhoto();
	/** Sets the photo of this object.
	 */
	public void setPhoto(Image photo) throws ModificationException;

	/** Returns the avatar (icon) of this object.
	 */
	public Image getAvatar();
	/** Sets the avator (icon) of this object.
	 */
	public void setAvatar(Image avatar) throws ModificationException;
}
