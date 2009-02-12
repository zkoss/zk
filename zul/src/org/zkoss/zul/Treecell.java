/* Treecell.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		Wed Jul  6 18:56:30     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.LabelImageElement;

/**
 * A treecell.
 *
 * <p>In XUL, treecell cannot have any child, but ZUL allows it.
 * Thus, you could place any kind of children in it. They will be placed
 * right after the image and label.
 *
 * <p>Default {@link #getZclass}: z-tree-cell.(since 3.5.0)
 * @author tomyeh
 */
public class Treecell extends LabelImageElement implements org.zkoss.zul.api.Treecell {
	private static final String ROOT_OPEN = "root-open";
	private static final String ROOT_CLOSE = "root-close";
	private static final String LAST_OPEN = "last-open";
	private static final String LAST_CLOSE = "last-close";
	private static final String TEE_OPEN = "tee-open";
	private static final String TEE_CLOSE = "tee-close";
	private static final String TEE = "tee";
	private static final String LAST = "last";
	private static final String VBAR = "vbar";
	private static final String SPACER = "spacer";
	private static final String FIRSTSPACER = "firstspacer";

	private int _span = 1;

	public Treecell() {
	}
	public Treecell(String label) {
		setLabel(label);
	}
	public Treecell(String label, String src) {
		setLabel(label);
		setImage(src);
	}

	/** Return the tree that owns this cell.
	 */
	public Tree getTree() {
		for (Component n = this; (n = n.getParent()) != null;)
			if (n instanceof Tree)
				return (Tree)n;
		return null;
	}
	/** Return the tree that owns this cell.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tree getTreeApi() {
		return getTree();
	}
	protected String getRealStyle() {
		final Treecol h = getTreecol();
		return isVisible() && h != null && !h.isVisible() ? super.getRealStyle() +
				"display:none;" : super.getRealStyle();
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
	/** Returns the tree col associated with this cell, or null if not available.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Treecol getTreecolApi() {
		return getTreecol();
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

	/** Returns number of columns to span this cell.
	 * Default: 1.
	 */
	public int getSpan() {
		return _span;
	}
	/** Sets the number of columns to span this cell.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span) {
		if (_span != span) {
			_span = span;
			smartUpdate("colspan", Integer.toString(_span));
		}
	}

	//-- Internal use only --//
	/** Returns the prefix of the first column (in HTML tags), null if this
	 * is not first column. Called only by treecell.jsp.
	 *
	 * <p>Used only for component template, not for application developers.
	 */
	public String getColumnHtmlPrefix() {
		if (isFirstColumn()) {
			final Treeitem item = getTreeitem();
			final Tree tree = getTree();
			final StringBuffer sb = new StringBuffer(80);
			if (tree != null) {
				if (tree.isCheckmark()) {
					final boolean isCheckable = item.isCheckable();
					sb.append("<input type=\"")
						.append(tree.isMultiple() ? "checkbox": "radio")
						.append('"');
					if (!isCheckable || item.isDisabled())
						sb.append(" disabled=\"disabled\"");
					if (item.isSelected())
						sb.append(" checked=\"checked\"");
					if (!tree.isMultiple())
						sb.append(" name=\"").append(tree.getUuid()).append("\"");
					if (!isCheckable)
						sb.append(" style=\"visibility:hidden;\"  z.fc=\"t\"/>");
					else 
						//NOTE: use Treerow's uuid! NOT Treeitem's!
						sb.append(" id=\"").append(getParent().getUuid())
							.append("!cm\" z.type=\"Tcfc\"/>");
				}
			}
			String iconScls = null;
			if (tree != null)
				iconScls = tree.getZclass();
			if (iconScls == null)
				iconScls = ""; //default

			final Treeitem[] pitems = getTreeitems(item);
			for (int j = 0; j < pitems.length; ++j)
				appendIcon(sb, iconScls,
					j == 0 || isLastChild(pitems[j]) ? SPACER: VBAR, false);

			if (item.isContainer()) {
				appendIcon(sb, iconScls,
					item.isOpen() ?
						pitems.length == 0 ? ROOT_OPEN:
							isLastChild(item) ? LAST_OPEN: TEE_OPEN:
						pitems.length == 0 ? ROOT_CLOSE:
							isLastChild(item) ? LAST_CLOSE: TEE_CLOSE,
						true);
			} else {
				appendIcon(sb, iconScls,
					pitems.length == 0 ? FIRSTSPACER:
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
		return null;
	}
	/** Returns whether this is the first column. */
	private boolean isFirstColumn() {
		final Component parent = getParent();
		return parent != null && parent.getFirstChild() == this;
	}

	/** Returns whether an item is the last child.
	 */
	public static boolean isLastChild(Treeitem item) {
		final Component parent = item.getParent();
		return parent == null || parent.getLastChild() == item;
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

	private Treeitem getTreeitem() {
		final Component parent = getParent();
		return parent != null ? (Treeitem)parent.getParent(): null;
	}

	public String getZclass() {
		return _zclass == null ? "z-tree-cell" : _zclass;
	}
	/** Generates HTML tags for &lt;img&gt;.
	 * @param button whether this is the button to toggle open/close
	 */
	private void appendIcon(StringBuffer sb, String iconScls,
	String name, boolean button) {
		sb.append("<span z.fc=\"t\" class=\"");
		if (name.equals(TEE) || name.equals(LAST) || name.equals(VBAR) || name.equals(SPACER)) {
			sb.append(iconScls+"-line ").append(iconScls).append('-').append(name).append('"');
		} else {
			sb.append(iconScls+"-ico ").append(iconScls).append('-').append(name).append('"');
		}
			//z.fc used to let tree.js know what to clone

		if (button) {
			final Treeitem item = getTreeitem();
			if (item != null) {
				final Treerow tr = item.getTreerow();
				if (tr != null)
					sb.append(" z.type=\"Tcop\" id=\"")
						.append(tr.getUuid()).append("!open\"");
			}
		}

		sb.append("></span>");
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


	/** Returns the attributes used by the embedded HTML LABEL tag.
	 * It returns text-relevant styles only.
	 * <p>Used only by component developer.
	 */
	public String getLabelAttrs() {
		final String style = HTMLs.getTextRelevantStyle(getRealStyle());
		return style.length() > 0 ? " style=\""+style+'"': "";
	}

	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();

		final Treecol col = getTreecol();
		final String clkattrs = getAllOnClickAttrs();
		if (col == null && clkattrs == null && _span == 1)
			return attrs;

		final StringBuffer sb = new StringBuffer(64).append(attrs);
		if (col != null) sb.append(col.getColAttrs());
		if (clkattrs != null) sb.append(clkattrs);
		if (_span != 1) HTMLs.appendAttribute(sb, "colspan", _span);
		return sb.toString();
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Treerow))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
}
