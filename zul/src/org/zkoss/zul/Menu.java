/* Menu.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:57:58     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.LabelImageElement;

/**
 * An element, much like a button, that is placed on a menu bar.
 * When the user clicks the menu element, the child {@link Menupopup}
 * of the menu will be displayed.
 * This element is also used to create submenus (of {@link Menupopup}.
 * 
 * <p>Default {@link #getZclass}: z-mean. (since 3.5.0)
 * @author tomyeh
 */
public class Menu extends LabelImageElement implements org.zkoss.zul.api.Menu {
	private Menupopup _popup;

	public Menu() {
	}
	public Menu(String label) {
		this();
		setLabel(label);
	}
	public Menu(String label, String src) {
		this();
		setLabel(label);
		setImage(src);
	}

	public String getImgTag() {
		return getImgTag(getZclass() + "-img", true);
	}
	/** Returns whether this is an top-level menu, i.e., not owning
	 * by another {@link Menupopup}.
	 */
	public boolean isTopmost() {
		return !(getParent() instanceof Menupopup);
	}

	/** Returns the {@link Menupopup} it owns, or null if not available.
	 */
	public Menupopup getMenupopup() {
		return _popup;
	}
	/** Returns the {@link Menupopup} it owns, or null if not available.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Menupopup getMenupopupApi() {
		return getMenupopup();		
	}

	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-menu" : _zclass;
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		if (_popup != null)
			HTMLs.appendAttribute(sb, "z.mpop", _popup.getUuid());
		if (isTopmost()) {
			sb.append(" z.top=\"true\"");
			final Component parent = getParent();
			if (parent instanceof Menubar
			&& "vertical".equals(((Menubar)parent).getOrient()))
				sb.append(" z.vert=\"true\"");
		}
		return sb.toString();
	}
	protected String getRealStyle() {
		final String style = super.getRealStyle();
		return isTopmost() ?
			style + "padding-left:4px;padding-right:4px;": style;
	}

	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Menubar)
		&& !(parent instanceof Menupopup))
			throw new UiException("Unsupported parent for menu: "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (child instanceof Menupopup) {
			if (_popup != null && _popup != child)
				throw new UiException("Only one menupopup is allowed: "+this);
			_popup = (Menupopup)child;
		} else {
			throw new UiException("Unsupported child for menu: "+child);
		}
		return super.insertBefore(child, insertBefore);
	}

	//Cloneable//
	public Object clone() {
		final Menu clone = (Menu)super.clone();
		if (clone._popup != null) clone.afterUnmarshal();
		return clone;
	}
	private void afterUnmarshal() {
		_popup = (Menupopup)getFirstChild();
	}

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		if (!getChildren().isEmpty()) afterUnmarshal();
	}
}
