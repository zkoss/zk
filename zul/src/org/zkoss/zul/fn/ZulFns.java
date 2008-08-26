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
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Attributes;
import org.zkoss.zul.Box;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Group;
import org.zkoss.zul.Groupfoot;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Row;
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
		if (child.isVisible() && !(child instanceof Listgroup) && !(child instanceof Group) 
			&& !(child instanceof Groupfoot) && !(child instanceof Listgroupfoot)){
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
	 * @param tree the tree
	 * @param child Treeitem
	 * @since 3.0.7
	 */
	public static final boolean shallVisitTree(Tree tree, Component child) {
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
	 * Returns whether the specified should be rendered.
	 * @param tree the tree
	 * @since 3.0.7
	 */
	public static final boolean shallRenderTree(Tree tree) {
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
	 * Clears up the attributes which are used to render a tree in paging mold
	 * @param tree the tree
	 * @since 3.0.7
	 */
	public static final void clearTreeRenderInfo(Tree tree) {
		tree.removeAttribute(Attributes.RENDERED_ITEM_COUNT);
		tree.removeAttribute(Attributes.VISITED_ITEM_COUNT);
		tree.removeAttribute(Attributes.VISITED_ITEM_TOTAL);
	}
	/**
	 * Returns the outer attributes of the specified component except CSS name and Style.
	 * @param cmp
	 * @since 3.5.0
	 */
	public static final String getZkOuterAttrs(HtmlBasedComponent cmp) {
		String attrs = cmp.getOuterAttrs();
		if (attrs == null || attrs.length() == 0) return attrs;
		final String CSS = "class=\"";
		final String STYLE = "style=\"";
		StringBuffer sb = new StringBuffer(attrs);
		int start = sb.indexOf(CSS);
		if (start > -1) {
			int end = sb.indexOf("\"", start + CSS.length());
			if (end > -1)
				sb.replace(start, end + 1, "");
		}
		start = sb.indexOf(STYLE);
		if (start > -1) {
			int end = sb.indexOf("\"", start + STYLE.length());
			if (end > -1)
				sb.replace(start, end + 1, "");
		}
		return sb.toString();
	}
}
