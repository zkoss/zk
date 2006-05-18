/* Imagemap.java

{{IS_NOTE
	$Id: Imagemap.java,v 1.1 2006/04/12 10:44:38 tomyeh Exp $
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
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.1 $ $Date: 2006/04/12 10:44:38 $
 */
public class Imagemap extends Image {
	//-- super --//
	protected boolean onClickAttrsRequired() {
		return false;
	}
	public String getInnerAttrs() {
		final String attrs = super.getInnerAttrs();
		return getChildren().isEmpty() ? attrs:
			attrs + " usemap=\"#" + getUuid() + "_map\"";
	}

	/** Default: childable.
	 */
	public boolean isChildable() {
		return true;
	}
	public boolean insertBefore(Component newChild, Component refChild) {
		if (!(newChild instanceof Area))
			throw new UiException("Unsupported child for Imagemap: "+newChild);
		if (getChildren().isEmpty())
			invalidate(INNER);
		return super.insertBefore(newChild, refChild);
	}
}
