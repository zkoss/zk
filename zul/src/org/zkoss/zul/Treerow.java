/* Treerow.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:56:22     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;

import org.zkoss.zul.impl.XulElement;

/**
 * A treerow.
 * <p>Default {@link #getZclass}: z-treerow (since 5.0.0)
 * @author tomyeh
 */
public class Treerow extends XulElement implements org.zkoss.zul.api.Treerow {
	/** Returns the {@link Tree} instance containing this element.
	 */
	public Tree getTree() {
		for (Component p = this; (p = p.getParent()) != null;)
			if (p instanceof Tree)
				return (Tree)p;
		return null;
	}
	/** Returns the {@link Tree} instance containing this element.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tree getTreeApi() {
		return getTree();
	}

	/** Returns the level this cell is. The root is level 0.
	 */
	public int getLevel() {
		final Component parent = getParent();
		return parent != null ? ((Treeitem)parent).getLevel(): 0;
	}

	/** Returns the {@link Treechildren} associated with this
	 * {@link Treerow}.
	 * In other words, it is {@link Treeitem#getTreechildren} of
	 * {@link #getParent}.
	 * @since 2.4.1
	 * @see Treechildren#getLinkedTreerow
	 */
	public Treechildren getLinkedTreechildren() {
		final Component parent = getParent();
		return parent != null ? ((Treeitem)parent).getTreechildren(): null;
	}
	/** Returns the {@link Treechildren} associated with this
	 * {@link Treerow}.
	 * In other words, it is {@link Treeitem#getTreechildren} of
	 * {@link #getParent}.
	 * @since 3.5.2
	 * @see Treechildren#getLinkedTreerow
	 */
	public org.zkoss.zul.api.Treechildren getLinkedTreechildrenApi() {
		return getLinkedTreechildren();
	}

	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-treerow" : _zclass;
	}

	/** Alwasys throws UnsupportedOperationException since developers shall
	 * use {@link Treeitem#setContext} instead.
	 */
	public void setContext(String context) {
		throw new UnsupportedOperationException("Use treeitem instead");
	}
	/** Alwasys throws UnsupportedOperationException since developers shall
	 * use {@link Treeitem#setPopup} instead.
	 */
	public void setPopup(String popup) {
		throw new UnsupportedOperationException("Use treeitem instead");
	}
	/** Alwasys throws UnsupportedOperationException since developers shall
	 * use {@link Treeitem#setTooltip} instead.
	 */
	public void setTooltip(String tooltip) {
		throw new UnsupportedOperationException("Use treeitem instead");
	}
	/** Returns the same as {@link Treeitem#getContext}.
	 */
	public String getContext() {
		final Treeitem ti = (Treeitem)getParent();
		return ti != null ? ti.getContext(): null;
	}
	/** Returns the same as {@link Treeitem#getPopup}.
	 */
	public String getPopup() {
		final Treeitem ti = (Treeitem)getParent();
		return ti != null ? ti.getPopup(): null;
	}
	/** Returns the same as {@link Treeitem#getTooltip}.
	 */
	public String getTooltip() {
		final Treeitem ti = (Treeitem)getParent();
		return ti != null ? ti.getTooltip(): null;
	}
	/** Returns the same as {@link Treeitem#getTooltiptext}
	 */
	public String getTooltiptext() {
		final Treeitem ti = (Treeitem)getParent();
		return ti != null ? ti.getTooltiptext(): null;
	}

	/*obsolete: protected boolean isAsapRequired(String evtnm) {
		if (!Events.ON_OPEN.equals(evtnm))
			return super.isAsapRequired(evtnm);
		final Treeitem ti = (Treeitem)getParent();
		return ti != null && ti.isAsapRequired(evtnm);
	}*/

	//-- Component --//
	/** Returns whether this is visible.
	 * whether all its ancestors is open.
	 */
	public boolean isVisible() {
		if (!super.isVisible())
			return false;
		Component comp = getParent();
		if (!(comp instanceof Treeitem))
			return true;
		if (!comp.isVisible()) return false;
		
		comp = comp.getParent();
		return !(comp instanceof Treechildren)
			|| ((Treechildren)comp).isVisible(); //recursive
	}
	
	private boolean isBothVisible() {
		if (!super.isVisible())
			return false;
		Component comp = getParent();
		if (!(comp instanceof Treeitem))
			return true;
		if (!comp.isVisible()) return false;
		return true;
	}
	
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Treeitem))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Treecell))
			throw new UiException("Unsupported child for tree row: "+child);
		super.beforeChildAdded(child, refChild);
	}}
