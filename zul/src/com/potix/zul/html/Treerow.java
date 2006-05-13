/* Treerow.java

{{IS_NOTE
	$Id: Treerow.java,v 1.11 2006/04/17 06:39:57 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:56:22     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.lang.Objects;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

import com.potix.zul.html.impl.XulElement;

/**
 * A treerow.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.11 $ $Date: 2006/04/17 06:39:57 $
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

	/** Returns the nestest parent {@link Treeitem}.
	 * It is the sames as {@link #getParent}.
	 */
	public Treeitem getTreeitem() {
		return (Treeitem)getParent();
	}

	//-- super --//
	/** Returns the style class.
	 * Note: 1) if not set (or setSclass(null), "item" is assumed;
	 * 2) if selected, it appends "sel" to super's getSclass().
	 */
	public String getSclass() {
		String scls = super.getSclass();
		if (scls == null) scls = "item";
		final Treeitem ti = getTreeitem();
		return ti != null && ti.isSelected() ? scls + "sel": scls;
	}

	protected boolean isAsapRequired(String evtnm) {
		if (!"onOpen".equals(evtnm))
			return super.isAsapRequired(evtnm);
		final Treeitem ti = getTreeitem();
		return ti != null && ti.isAsapRequired(evtnm);
	}
	/** Appends attributes for generating the real checkbox HTML tags
	 * (name="val"); Used only by component developers.
	 */
	public String getOuterAttrs() {
		final Treeitem item = getTreeitem();
		if (item == null)
			return super.getOuterAttrs();

		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());
		final Treeitem ptitem = item.getTreeitem();

		final Tree tree = getTree();
		if (tree != null && tree.getName() != null)
			HTMLs.appendAttribute(sb, "zk_value",  Objects.toString(item.getValue()));
		HTMLs.appendAttribute(sb, "zk_item", item.getUuid());
		HTMLs.appendAttribute(
			sb, "zk_ptitem", ptitem != null ? ptitem.getUuid(): "root");
				//zk_ptitem: parent item
		HTMLs.appendAttribute(sb, "zk_sel", item.isSelected());
		if (item.isContainer())
			HTMLs.appendAttribute(sb, "zk_open", item.isOpen());

		if (item.isFocusRequired())
			sb.append(" zk_focus=\"true\"");
		if (isAsapRequired("onOpen"))
			sb.append(" zk_onOpen=\"true\"");
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
		comp = comp.getParent();
		return !(comp instanceof Treechildren)
			|| ((Treechildren)comp).isVisible(); //recursive
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
	public void invalidate(Range range) {
		super.invalidate(range);
		initAtClient();
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		initAtClient();
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		initAtClient();
	}
	private void initAtClient() {
		final Tree tree = getTree();
		if (tree != null) tree.initAtClient();
	}
}
