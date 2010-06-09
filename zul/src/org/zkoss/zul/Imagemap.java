/* Imagemap.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 28 00:25:48     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * An image map.
 *
 * <p>There are two ways to use Imagemap:</p>
 *
 * <ol>
 * <li>Listen to the onClick event, which is an instance of
 * {@link org.zkoss.zk.ui.event.MouseEvent}. Then, you could call
 * getX() and getY() to retrieve where user has clicked.</li>
 * <li>Assign one or multiple of {@link Area} as its children.
 * Then, listen to the onClick event, and use
 * {@link org.zkoss.zk.ui.event.MouseEvent#getArea} to retrieve
 * which area is clicked.</li>
 * </ol>
 *
 * @author tomyeh
 */
public class Imagemap extends Image implements org.zkoss.zul.api.Imagemap{
	//-- super --//
	/** Default: childable.
	 */
	protected boolean isChildable() {
		return true;
	}
	public void beforeChildAdded(Component newChild, Component refChild) {
		if (!(newChild instanceof Area))
			throw new UiException("Unsupported child for imagemap: "+newChild);
		super.beforeChildAdded(newChild, refChild);
	}
}
