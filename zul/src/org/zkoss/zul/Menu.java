/* Menu.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:57:58     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.impl.LabelImageElement;
import org.zkoss.zul.impl.Utils;

/**
 * An element, much like a button, that is placed on a menu bar.
 * When the user clicks the menu element, the child {@link Menupopup}
 * of the menu will be displayed.
 * This element is also used to create submenus (of {@link Menupopup}.
 * 
 * <p>Default {@link #getSclass}: z-mean. (since 3.5.0)
 * @author tomyeh
 */
public class Menu extends LabelImageElement {
	private Menupopup _popup;

	public Menu() {
		if (Utils.isThemeV30()) setMold("v30");
		setSclass("z-menu");
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
		if ("v30".equals(getMold()))
			return super.getImgTag();
		else {
			final String src = getImageContent() != null ? getContentSrc():
				getDesktop().getExecution().encodeURL(getSrc() != null ? getSrc() : "~./img/spacer.gif");
				
			final StringBuffer sb = new StringBuffer(64)
				.append("<img class=\"").append(getSclass()).append("-item-icon\" src=\"")
				.append(src).append("\" align=\"absmiddle\"/>");

			final String label = getLabel();
			if (label != null && label.length() > 0) sb.append(' ');

			return sb.toString(); //keep a space
		}
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

	//-- Component --//
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
