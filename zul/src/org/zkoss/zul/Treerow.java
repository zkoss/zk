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
 *
 * @author tomyeh
 */
public class Treerow extends XulElement {
	/** Returns the {@link Tree} instance containing this element.
	 */
	public Tree getTree() {
		for (Component p = this; (p = p.getParent()) != null;)
			if (p instanceof Tree)
				return (Tree)p;
		return null;
	}

	/** Returns the level this cell is. The root is level 0.
	 */
	public int getLevel() {
		final Component parent = getParent();
		return parent != null ? ((Treeitem)parent).getLevel(): 0;
	}

	/** Returns the parent {@link Treeitem}.
	 * @deprecated As of release 2.4.1, due to confusing
	 */
	public Treeitem getTreeitem() {
		return (Treeitem)getParent();
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

	//-- super --//
	/** Returns the style class.
	 * Note: 1) if not set (or setSclass(null), "item" is assumed;
	 * 2) if selected, it appends " seld" to super's getSclass().
	 */
	public String getSclass() {
		String scls = super.getSclass();
		if (scls == null) scls = "item";
		final Treeitem ti = getTreeitem();
		if (ti != null) {
			if (ti.isDisabled())
				return scls.length() > 0 ? scls + " disd": "disd";
			else if (ti.isSelected())
				return scls.length() > 0 ? scls + " seld": "seld";
		}
		return scls;
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
		final Treeitem ti = getTreeitem();
		return ti != null ? ti.getContext(): null;
	}
	/** Returns the same as {@link Treeitem#getPopup}.
	 */
	public String getPopup() {
		final Treeitem ti = getTreeitem();
		return ti != null ? ti.getPopup(): null;
	}
	/** Returns the same as {@link Treeitem#getTooltip}.
	 */
	public String getTooltip() {
		final Treeitem ti = getTreeitem();
		return ti != null ? ti.getTooltip(): null;
	}
	/** Returns the same as {@link Treeitem#getTooltiptext}
	 */
	public String getTooltiptext() {
		final Treeitem ti = getTreeitem();
		return ti != null ? ti.getTooltiptext(): null;
	}

	protected boolean isAsapRequired(String evtnm) {
		if (!Events.ON_OPEN.equals(evtnm))
			return super.isAsapRequired(evtnm);
		final Treeitem ti = getTreeitem();
		return ti != null && ti.isAsapRequired(evtnm);
	}
	/** Appends attributes for generating the real checkbox HTML tags
	 * (name="val"); Used only by component developers.
	 */
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final Treeitem item = getTreeitem();
		if (item == null) return attrs;

		final StringBuffer sb = new StringBuffer(80).append(attrs);

		final Tree tree = getTree();
		if (tree != null && tree.getName() != null)
			HTMLs.appendAttribute(sb, "z.value",  Objects.toString(item.getValue()));
		HTMLs.appendAttribute(sb, "z.pitem", item.getUuid());
		HTMLs.appendAttribute(sb, "z.sel", item.isSelected());
		HTMLs.appendAttribute(sb, "z.disd", item.isDisabled());
		HTMLs.appendAttribute(sb, "z.rid", tree.getUuid());
		if (item.isContainer())
			HTMLs.appendAttribute(sb, "z.open", item.isOpen());

		final Component gp = item.getParent(); //Treechildren
		if (gp != null) {
			HTMLs.appendAttribute(sb, "z.ptch", gp.getUuid());
			Component gpitem = gp.getParent();
			if (gpitem instanceof Treeitem)
				HTMLs.appendAttribute(sb, "z.gpitem", gpitem.getUuid());
		}

		final Treechildren tcsib = getLinkedTreechildren();
		if (tcsib != null) {
			HTMLs.appendAttribute(sb, "z.tchsib", tcsib.getUuid());
		}

		if (tree != null && tree.getModel() != null && !item.isLoaded())
			sb.append(" z.lod=\"t\""); //lod=Load-on-Demand
		if (getTree().inPagingMold())
			HTMLs.appendAttribute(sb, "z."+Events.ON_OPEN, true);
		else appendAsapAttr(sb, Events.ON_OPEN);
		final String clkattrs = getAllOnClickAttrs();
		if (clkattrs != null) sb.append(clkattrs);
		HTMLs.appendAttribute(sb, "z.visible", isBothVisible());
		return sb.toString();
	}

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
	public boolean setVisible(boolean visible) {
		if (isVisible() != visible) 
			smartUpdate("z.visible", visible);
		return super.setVisible(visible);
	}
	
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Treeitem))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Treecell))
			throw new UiException("Unsupported child for tree row: "+child);
		return super.insertBefore(child, insertBefore);
	}
}
