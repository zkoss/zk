/* Imagemap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * An image map.
 * 
 * <p>
 * There are two ways to use Imagemap:
 * </p>
 * 
 * <ol>
 * <li>Listen to the onClick event, which is an instance of
 * {@link org.zkoss.zk.ui.event.MouseEvent}. Then, you could call getX() and
 * getY() to retrieve where user has clicked.</li>
 * <li>Assign one or multiple of {@link Area} as its children. Then, listen to
 * the onClick event, and use {@link org.zkoss.zk.ui.event.MouseEvent#getArea}
 * to retrieve which area is clicked.</li>
 * </ol>
 * 
 * <p>
 * Note: IE 5.5/6 (not 7) has a bug that failed to render PNG with alpha
 * transparency. See http://homepage.ntlworld.com/bobosola/index.htm for
 * details. Thus, if you want to display such image, you have to use the
 * alphafix mold. <code><imagemap mold="alphafix"/></code>
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Imagemap extends Image {

}
