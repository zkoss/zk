/* Caption.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 11 14:31:07     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.LabelImageElement;

/**
 *  A header for a {@link Groupbox}.
 * It may contain either a text label, using {@link #setLabel},
 * or child elements for a more complex caption.
 *
 * @author tomyeh
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
	 *
	 * <p>It is mainly used for component implementation.
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
	 * It actually returns {@link Groupbox#isLegend} if the parent
	 * is a {@link Groupbox}.
	 *
	 * <p>Note: this is designed to used for component templating.
	 * Application developers rarely need to access this method.
	 */
	public boolean isLegend() {
		final Component p = getParent();
		return (p instanceof Groupbox) && ((Groupbox)p).isLegend();
	}
	/** Returns whether to display the closable button.
	 * <p>Default: it returns true if the parent is window and {@link Window#isClosable}
	 * is true.
	 *
	 * <p>It is mainly used for component implementation.
	 */
	public boolean isClosableVisible() {
		final Component p = getParent();
		return (p instanceof Window) && ((Window)p).isClosable();
	}

	//-- super --//
	/** Returns the style class.
	 * <p>Default: return caption if the parent is groupbox,
	 * or title otherwise (say, the parent is window)
	 */
	public String getSclass() {
		final String scls = super.getSclass();
		if (scls != null) return scls;
		return getParent() instanceof Groupbox ? "caption": "title";
	}
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String clkattrs = getAllOnClickAttrs();
		return clkattrs == null ? attrs: attrs + clkattrs;
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Window)
		&& !(parent instanceof Groupbox))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public void invalidate() {
		final Component p = getParent();
		if ((p instanceof Groupbox) && ((Groupbox)p).isLegend())
			p.invalidate(); //Bug 1679629
		else
			super.invalidate();
	}
}
