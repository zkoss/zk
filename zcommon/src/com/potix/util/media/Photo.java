/* Photo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun  8 10:48:36     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.media;

import com.potix.image.Image;
import com.potix.util.ModificationException;

/**
 * Represents an object is associated with images.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
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
