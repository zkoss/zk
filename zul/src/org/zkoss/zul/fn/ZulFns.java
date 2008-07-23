/* ZulFns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep 12 15:19:42     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul.fn;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Attributes;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Box;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.ext.Paginal;

/**
 * Utilities for using EL.
 * 
 * @author tomyeh
 */
public class ZulFns {
	protected ZulFns() {}

	/** Returns the column attribute of a child of a row by specifying
	 * the index.
	 */
	public static final String getColAttrs(Row row, int index) {
		return row.getChildAttrs(index);
	}

	/**
	 * Returns the inner attributes used for the cell of the specified child
	 * when it is placed inside of hbox/vbox.
	 */
	public static final String getBoxChildInnerAttrs(Component child) {
		return ((Box)child.getParent()).getChildInnerAttrs(child);
	}
	/**
	 * Returns the outer attributes used for the cell of the specified child
	 * when it is placed inside of hbox/vbox.
	 */
	public static final String getBoxChildOuterAttrs(Component child) {
		return ((Box)child.getParent()).getChildOuterAttrs(child);
	}
	
	/**
	 * Sets the stripe CSS for each row.
	 */
	public static final void setStripeClass(Component child) {
		final Component parent = child.getParent();
		if (child.isVisible()) {
			final String odd = (String)parent.getAttribute(Attributes.STRIPE_STATE);
			if (odd == null || !odd.equals("")) {
				parent.setAttribute(Attributes.STRIPE_STATE, "");
			} else {
				if (parent instanceof Listbox)
					parent.setAttribute(Attributes.STRIPE_STATE, ((Listbox)parent).getOddRowSclass());
				else
					parent.setAttribute(Attributes.STRIPE_STATE, ((Grid)parent.getParent()).getOddRowSclass());
			}
				
		}
	}
	
	/**
	 * Resets the stripe CSS for each row.
	 * @since 3.0.3
	 */
	public static final void resetStripeClass(Component parent) {
		parent.removeAttribute(Attributes.STRIPE_STATE);
	}
	/**
	 * Returns whether the treeitem should be visited.
	 * @param root Tree
	 * @param child Treeitem
	 * @since 3.0.7
	 */
	public static final boolean shouldBeVisited(Component root, Component child) {
		final Tree tree = (Tree) root;
		final Treeitem item = (Treeitem) child;
		int count = item.isOpen() && item.getTreechildren() != null ? 
				item.getTreechildren().getVisibleItemCount(): 0;
		Integer visited = (Integer)tree.getAttribute(Attributes.VISITED_ITEM_COUNT);
		final Paginal pgi = tree.getPaginal();
		final int ofs = pgi.getActivePage() * pgi.getPageSize();
		int visit = visited != null ? visited.intValue() + 1 : 1;
		boolean shoulbBeVisited = ofs < visit + count;
		if (visited == null) visited = new Integer(shoulbBeVisited ? 1 : count + 1);
		else visited = new Integer(visited.intValue()+ (shoulbBeVisited ? 1 : count + 1));

		Integer total = (Integer)tree.getAttribute(Attributes.VISITED_ITEM_TOTAL);
		if (total == null) total = new Integer(count + 1);
		else total = new Integer(total.intValue() + count + 1);
		tree.setAttribute(Attributes.VISITED_ITEM_COUNT, visited);
		tree.setAttribute(Attributes.VISITED_ITEM_TOTAL, total);
		return shoulbBeVisited;
	}
	/**
	 * Returns whether the treeitem should be rendered.
	 * @param root Tree
	 * @since 3.0.7
	 */
	public static final boolean shouldBeRendered(Component root) {
		final Tree tree = (Tree) root;
		Integer visited = (Integer)tree.getAttribute(Attributes.VISITED_ITEM_COUNT);
		final Paginal pgi = tree.getPaginal();
		final int ofs = pgi.getActivePage() * pgi.getPageSize();
		if(ofs < visited.intValue()) {
			// count the rendered item
			Integer renderedCount = (Integer) tree.getAttribute(Attributes.RENDERED_ITEM_COUNT);
			if (renderedCount == null) renderedCount = new Integer(1);
			else renderedCount = new Integer(renderedCount.intValue() + 1);
			tree.setAttribute(Attributes.RENDERED_ITEM_COUNT, renderedCount);
			return true;
		}
		return false;
	}
	/**
	 * Clears up these attributes which are used for Tree's paging mold
	 * @param tree Tree
	 * @since 3.0.7
	 */
	public static final void clearRenderedItem(Component tree) {
		((Tree)tree).setAttribute(Attributes.RENDERED_ITEM_COUNT, null);
		((Tree)tree).setAttribute(Attributes.VISITED_ITEM_COUNT, null);
		((Tree)tree).setAttribute(Attributes.VISITED_ITEM_TOTAL, null);
	}
}
