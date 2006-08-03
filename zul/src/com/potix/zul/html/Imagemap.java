/* Imagemap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Mar 28 00:25:48     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

/**
 * An image map.
 *
 * <p>There are two ways to use Imagemap:</p>
 *
 * <ol>
 * <li>Listen to the onClick event, which is an instance of
 * {@link com.potix.zk.ui.event.MouseEvent}. Then, you could call
 * getX() and getY() to retrieve where user has clicked.</li>
 * <li>Assign one or multiple of {@link Area} as its children.
 * Then, listen to the onClick event, and use
 * {@link com.potix.zk.ui.event.MouseEvent#getArea} to retrieve
 * which area is clicked.</li>
 * </ol>
 *
 * <p>Note: IE 5.5/6 (not 7) has a bug that failed to render PNG with
 * alpha transparency. See http://homepage.ntlworld.com/bobosola/index.htm for details.
 * Thus, if you want to display such image, you have to use the alphafix mold.
 * <code>&lt;imagemap mold="alphafix"/&gt;</code>
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Imagemap extends Image {
	//-- super --//
	public String getOuterAttrs() {
		//Imagemap handles onclick by itself, so don't generate zk_lfclk
		final String attrs = super.getOuterAttrs();
		final String attrnm = " zk_lfclk=";
		final int j = attrs.indexOf(attrnm);
		if (j < 0) return attrs;
		int k = attrs.indexOf('"', j + attrnm.length());
		assert k > 0: attrs;
		k = attrs.indexOf('"', k + 1);
		assert k > 0: attrs;
		return attrs.substring(0, j) + attrs.substring(k + 1);
	}

	/** Default: childable.
	 */
	public boolean isChildable() {
		return true;
	}
	public boolean insertBefore(Component newChild, Component refChild) {
		if (!(newChild instanceof Area))
			throw new UiException("Unsupported child for imagemap: "+newChild);
		return super.insertBefore(newChild, refChild);
	}
}
