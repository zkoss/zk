/* Menubar.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:34:31     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.lang.JVMs;
import org.zkoss.lang.Objects;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.impl.XulElement;

/**
 * A container that usually contains menu elements.
 *
 * <p>Default {@link #getZclass}: z-menubar-hor, if {@link #getOrient()} == vertical,
 *  z-menubar-ver will be added.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Menubar extends XulElement implements org.zkoss.zul.api.Menubar {
	private boolean _autodrop;
	private String _orient = "horizontal";
	private boolean _scrollable = false;

	public Menubar() {
	}
	/**
	 * @param orient either horizontal or vertical
	 */
	public Menubar(String orient) {
		this();
		setOrient(orient);
	}
	
	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return _orient;
	}
	/** Sets the orient.
	 * @param orient either horizontal or vertical
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("orient cannot be "+orient);
		
		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			invalidate();
		}
	}
	
	/**
	 * Returns whether it is a horizontal .
	 * @since 3.6.3
	 */
	public boolean isHorizontal() {
		return "horizontal".equals(getOrient());
	}

	/**
	 * Returns whether it is a vertical .
	 * @since 3.6.3
	 */
	public boolean isVertical() {
		return "vertical".equals(getOrient());
	}
	
	/**
	 * Returns whether the menubar scrolling is enabled. 
	 * <p>Default: false.
	 * @since 3.6.3
	 */
	public boolean isScrollable(){
		return _scrollable;
	}
	
	/**
	 * Sets whether to enable the menubar scrolling
	 * @since 3.6.3
	 */
	public void setScrollable(boolean scrollable){
		if (isHorizontal() && _scrollable != scrollable) {
			_scrollable = scrollable;
			invalidate();
		}
	}

	/** Returns whether to automatically drop down menus if user moves mouse
	 * over it.
	 * <p>Default: false.
	 */
	public final boolean isAutodrop() {
		return _autodrop;
	}
	/** Sets whether to automatically drop down menus if user moves mouse
	 * over it.
	 */
	public void setAutodrop(boolean autodrop) {
		if (_autodrop != autodrop) {
			_autodrop = autodrop;
			smartUpdate("z.autodrop", autodrop);
		}
	}

	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-menubar" +
				("vertical".equals(getOrient()) ? "-ver" : "-hor") : _zclass;
	}
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		if (_autodrop)
			HTMLs.appendAttribute(sb, "z.autodrop", _autodrop);
		if (isHorizontal() && _scrollable)
			HTMLs.appendAttribute(sb, "z.scrollable", _scrollable);
		
		return sb.toString();
	}
	
	public void onChildAdded(Component child) {
		if (isHorizontal() && _scrollable)
			smartUpdate("z.chchg", true);
		super.onChildAdded(child);
	}
	
	public void onChildRemoved(Component child) {
		if (isHorizontal() && _scrollable)
			smartUpdate("z.chchg", true);
		super.onChildRemoved(child);
	}
	
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Menu) && !(child instanceof Menuitem) && !(child instanceof Menuseparator))
			throw new UiException("Unsupported child for menubar: "+child);
		super.beforeChildAdded(child, refChild);
	}

	public void onDrawNewChild(Component child, StringBuffer out)
	throws IOException {
		if ("vertical".equals(getOrient())) {
			final StringBuffer sb = new StringBuffer(32)
				.append("<tr id=\"").append(child.getUuid()).append("!chdextr\"");
			if (child instanceof HtmlBasedComponent) {
				final String height = ((HtmlBasedComponent)child).getHeight();
				if (height != null)
					sb.append(" height=\"").append(height).append('"');
			}
			sb.append('>');
			if (JVMs.isJava5()) out.insert(0, sb); //Bug 1682844
			else out.insert(0, sb.toString());
			out.append("</tr>");
		}
	}
}
