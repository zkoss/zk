/* Caption.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 11 14:31:07     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

import com.potix.zul.html.impl.LabelImageElement;

/**
 *  A header for a {@link Groupbox}.
 * It may contain either a text label, using {@link #setLabel},
 * or child elements for a more complex caption.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.7 $ $Date: 2006/05/29 04:28:21 $
 */
public class Caption extends LabelImageElement {
	public Caption() {
	}
	public Caption(String label) {
		setLabel(label);
	}
	public Caption(String label, String src) {
		setLabel(label);
		setImage(src);
	}

	/** Returns a compound label, which is the catenation of
	 * parent's title, if the parent is {@link Window}, and {@link #getLabel}.
	 * <p>Note: this is designed to used for component templating.
	 * Application developers rarely need to access this method.
	 */
	public String getCompoundLabel() {
		final String label = getLabel();
		final Component p = getParent();
		if (p instanceof Window) {
			final String title = ((Window)p).getTitle();
			if (title.length() > 0)
				return label.length() > 0 ? title + " - " + label: title;
		}
		return label;
	}
	/** Returns whether the legend mold shall be used.
	 * <p>Note: this is designed to used for component templating.
	 * Application developers rarely need to access this method.
	 */
	public boolean isLegend() {
		final Component p = getParent();
		return (p instanceof Groupbox) && "default".equals(p.getMold());
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Window)
		&& !(parent instanceof Groupbox))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
}
