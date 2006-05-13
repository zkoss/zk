/* Treecell.java

{{IS_NOTE
	$Id: Treecell.java,v 1.18 2006/03/17 10:06:38 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:56:30     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import com.potix.xml.HTMLs;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

import com.potix.zul.html.impl.LabelImageElement;

/**
 * A treecell.
 *
 * <p>In XUL, treecell cannot have any child, but ZUL allows it.
 * Thus, you could place any kind of children in it. They will be placed
 * right after the image and label.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.18 $ $Date: 2006/03/17 10:06:38 $
 */
public class Treecell extends LabelImageElement {
	private static final String ROOT_OPEN = "/root-open.gif";
	private static final String ROOT_CLOSE = "/root-close.gif";
	private static final String LAST_OPEN = "/last-open.gif";
	private static final String LAST_CLOSE = "/last-close.gif";
	private static final String TEE_OPEN = "/tee-open.gif";
	private static final String TEE_CLOSE = "/tee-close.gif";
	private static final String TEE = "/tee.gif";
	private static final String LAST = "/last.gif";
	private static final String VBAR = "/vbar.gif";
	private static final String SPACER = "/spacer.gif";

	public Treecell() {
	}
	public Treecell(String label) {
		setLabel(label);
	}
	public Treecell(String label, String src) {
		setLabel(label);
		setSrc(src);
	}

	/** Return the tree that owns this cell.
	 */
	public Tree getTree() {
		for (Component n = this; (n = n.getParent()) != null;)
			if (n instanceof Tree)
				return (Tree)n;
		return null;
	}
	/** Returns the tree col associated with this cell, or null if not available.
	 */
	public Treecol getTreecol() {
		final Tree tree = getTree();
		if (tree != null) {
			final Treecols lcs = tree.getTreecols();
			if (lcs != null) {
				final int j = getColumnIndex();
				final List lcschs = lcs.getChildren();
				if (j < lcschs.size())
					return (Treecol)lcschs.get(j);
			}
		}
		return null;
	}
	/** Returns the column index of this cell, starting from 0.
	 */
	public int getColumnIndex() {
		int j = 0;
		for (Iterator it = getParent().getChildren().iterator();
		it.hasNext(); ++j)
			if (it.next() == this)
				break;
		return j;
	}

	/** Returns the maximal length for this cell, which is decided by
	 * the corresponding {@link #getTreecol}'s {@link Treecol#getMaxlength}.
	 *
	 * <p>Note: DBCS counts  two bytes (range 0x4E00~0x9FF).
	 * Default: 0 (no limit).
	 */
	public int getMaxlength() {
		final Tree tree = getTree();
		if (tree == null)
			return 0;
		final Treecol lc = getTreecol();
		return lc != null ? lc.getMaxlength(): 0;
	}

	/** Returns the level this cell is. The root is level 0.
	 */
	public int getLevel() {
		final Component parent = getParent();
		return parent != null ? ((Treerow)parent).getLevel(): 0;
	}

	//-- Internal use only --//
	/** Returns the prefix of the first column (in HTML tags), null if this
	 * is not first column. Called only by treecell.jsp.
	 */
	public String getColumnHtmlPrefix() {
		if (isFirstColumn()) {
			final Treeitem item = getTreeitem();
			final Tree tree = getTree();
			final StringBuffer sb = new StringBuffer(80);
			if (tree != null && tree.isCheckmark()) {
				sb.append("<input type=\"")
					.append(tree.isMultiple() ? "checkbox": "radio")
					.append('"');
				if (item.isSelected())
					sb.append(" checked=\"checked\"");
				sb.append(" id=\"")
					.append(getParent().getUuid()).append("!cm\"")
					.append(" zk_type=\"Tcfc\"").append("/>");
					//NOTE: use Treerow's uuid! NOT Treeitem's!
			}

			final Treeitem[] pitems = getTreeitems(item);
			for (int j = 0; j < pitems.length; ++j)
				appendImage(sb,
					j == 0 || isLastChild(pitems[j]) ? SPACER: VBAR, false);
	
			if (item.isContainer()) {
				appendImage(sb,
					item.isOpen() ?
						pitems.length == 0 ? ROOT_OPEN:
							isLastChild(item) ? LAST_OPEN: TEE_OPEN:
						pitems.length == 0 ? ROOT_CLOSE:
							isLastChild(item) ? LAST_CLOSE: TEE_CLOSE,
						true);
			} else {
				appendImage(sb,
					pitems.length == 0 ? SPACER:
						isLastChild(item) ? LAST: TEE, false);
			}
			return sb.toString();
		} else {
			//To make the tree's height more correct, we have to generate &nbsp;
			//for empty cell. Otherwise, IE will make the height too small
			final boolean empty = getImage() == null && getLabel().length() == 0
				&& getChildren().isEmpty();
			return empty ? "&nbsp;": null;
		}
	}
	/** Returns the postfix of the first column (in HTML tags), null if this
	 * is not first column. Called only by treecell.jsp.
	 */
	public String getColumnHtmlPostfix() {
		final Treeitem item = getTreeitem();
		if (isFirstColumn() && item.isFocusRequired()) {
			final StringBuffer sb = new StringBuffer(64)
				.append("<a href=\"javascript:;\" id=\"")
				.append(getParent().getUuid()).append("!sel\"")
				.append(" zk_type=\"Tcfc\"").append("> </a>");
					//NOTE: use Treerow's uuid! NOT Treeitem's!
			return sb.toString();
		} else { 
			return null;
		}
	}
	/** Returns whether this is the first column. */
	private boolean isFirstColumn() {
		final Component parent = getParent();
		return parent != null && parent.getChildren().get(0) == this;
	}

	/** Returns whether an item is the last child.
	 */
	public boolean isLastChild(Treeitem item) {
		final Component parent = item.getParent();
		if (parent == null)
			return true;

		final List sibs = parent.getChildren();
		return sibs.get(sibs.size() - 1) == item;
	}
	/** Returns an array of Treeitem from the root.
	 */
	private Treeitem[] getTreeitems(Component item) {
		final List pitems = new LinkedList();
		for (;;) {
			final Component tch = item.getParent();
			if (tch == null)
				break;
			item = tch.getParent();
			if (item == null || item instanceof Tree)
				break;
			pitems.add(0, item);
		}
		return (Treeitem[])pitems.toArray(new Treeitem[pitems.size()]);
	}

	/** Returns the nestest parent {@link Treeitem}.
	 */
	public Treeitem getTreeitem() {
		final Component parent = getParent();
		return parent != null ? (Treeitem)parent.getParent(): null;
	}

	/** Generates HTML tags for &lt;img&gt;.
	 * @param button whether this is the button to toggle open/close
	 */
	private void appendImage(StringBuffer sb, String name, boolean button) {
		final int width = getIconWidth(), height = getIconHeight();
		final String uri = getIconURI(name);
		sb.append("<img align=\"top\"");
		HTMLs.appendAttribute(
			sb, "src", getDesktop().getExecution().encodeURL(uri));
		HTMLs.appendAttribute(sb, "width", width);
		HTMLs.appendAttribute(sb, "height", height);
		if (button) {
			final Treeitem item = getTreeitem();
			if (item != null) {
				sb.append(" zk_type=\"Tcop\"");
				HTMLs.appendAttribute(sb,
					"id", item.getTreerow().getUuid() + "!open");
			}

			//HTMLs.appendAttribute(sb, "title", title);
		}
		sb.append("/>");
	}

	/** Returns the width of the icon. */
	private int getIconWidth() {
		return getRequiredIntInitParam("icon-width");
	}
	/** Returns the height of the icon. */
	private int getIconHeight() {
		return getRequiredIntInitParam("icon-height");
	}
	/** Returns the icon URI.
	 */
	private String getIconURI(String name) {
		final String s = getInitParam("icon-uri");
		if (s == null)
			throw new UiException("The icon-uri param must be defined");
		return s + name;
	}
	private int getRequiredIntInitParam(String name) {
		final int v = getIntInitParam(name);
		if (v <= 0)
			throw new UiException("The "+name+" param must be defined and positive");
		return v;
	}

	//-- super --//
	/** Returns the width which the same as {@link #getTreecol}'s width.
	 */
	public String getWidth() {
		final Treecol col = getTreecol();
		return col != null ? col.getWidth(): null;
	}
	public void setWidth(String width) {
		throw new UnsupportedOperationException("Set treecol's width instead");
	}
	public String getOuterAttrs() {
		final Treecol col = getTreecol();
		final String attrs = super.getOuterAttrs();
		return col != null ? attrs+col.getColAttrs(): attrs;
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Treerow))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
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
