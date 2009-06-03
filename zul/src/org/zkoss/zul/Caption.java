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

import org.zkoss.zul.ext.Framable;
import org.zkoss.zul.impl.LabelImageElement;

/**
 *  A header for a {@link Groupbox}.
 * It may contain either a text label, using {@link #setLabel},
 * or child elements for a more complex caption.
 * <p>Default {@link #getZclass}: z-caption.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Caption extends LabelImageElement implements org.zkoss.zul.api.Caption {
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
	 * <p>Default: it returns true if the parent is framable and {@link Framable#isClosable}
	 * is true.
	 *
	 * <p>It is mainly used for component implementation.
	 */
	public boolean isClosableVisible() {
		final Component p = getParent();
		return (p instanceof Framable) && ((Framable)p).isClosable();
	}
	/** Returns whether to display the toggle button.
	 * <p>Default: it returns true if the parent is framable and {@link Framable#isClosable}
	 * is true.
	 *
	 * <p>It is mainly used for component implementation.
	 * @since 3.6.2
	 */
	public boolean isCollapsibleVisible() {
		final Component p = getParent();
		return (p instanceof Framable) && ((Framable)p).isCollapsible();
	}
	/** Returns whether to display the maximizable button.
	 * <p>Default: it returns true if the parent is framable and {@link Framable#isMaximizable}
	 * is true.
	 *
	 * <p>It is mainly used for component implementation.
	 * @since 3.5.1
	 */
	public boolean isMaximizableVisible() {
		final Component p = getParent();
		return (p instanceof Framable) && ((Framable)p).isMaximizable();
	}
	/** Returns whether to display the minimizable button.
	 * <p>Default: it returns true if the parent is framable and {@link Framable#isMinimizable}
	 * is true.
	 *
	 * <p>It is mainly used for component implementation.
	 * @since 3.5.1
	 */
	public boolean isMinimizableVisible() {
		final Component p = getParent();
		return (p instanceof Framable) && ((Framable)p).isMinimizable();
	}
	// super
	public String getZclass() {
		return _zclass == null ? "z-caption" : _zclass;
	}
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String clkattrs = getAllOnClickAttrs();
		return clkattrs == null ? attrs: attrs + clkattrs;
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Window)
		&& !(parent instanceof Groupbox) && !(parent instanceof Panel))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}
	public void invalidate() {
		final Component p = getParent();
		if ((p instanceof Groupbox) && ((Groupbox)p).isLegend())
			p.invalidate(); //Bug 1679629
		else
			super.invalidate();
	}
}
