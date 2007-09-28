/* Image.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 5, 2007 6:06:55 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkforge.yuiext.grid;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;

/**
 *  * This class is implemented for Ext JS. And it extends the original
 * {@link org.zkoss.zul.Image}.
 * @author jumperchen
 *
 */
public class Image extends org.zkoss.zul.Image {
	public Image() {
	}
	public Image(String src) {
		setSrc(src);
	}
	protected void addMoved(Component oldparent, Page oldpg, Page newpg) {
		if (getParent() != null && getParent() instanceof Row) {
			if (((Row) getParent()).isSmartUpdate()) {
				super.addMoved(oldparent, oldpg, newpg);
			}
		}else {
			super.addMoved(oldparent, oldpg, newpg);
		}
	}
}
