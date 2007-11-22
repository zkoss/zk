/* Treecell.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:56:30     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.lang.Objects;
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
 * @author tomyeh
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
					sb.append("<input type=\"")
						.append(tree.isMultiple() ? "checkbox": "radio")
						.append('"');
					if (item.isSelected())
						sb.append(" checked=\"checked\"");
	
					//NOTE: use Treerow's uuid! NOT Treeitem's!
					sb.append(" id=\"").append(getParent().getUuid())
						.append("!cm\" z.type=\"Tcfc\"/>");
				} else if (item.isFocusRequired()) {
					//NOTE: use Treerow's uuid! NOT Treeitem's!
					sb.append("<a href=\"javascript:;\" id=\"").append(getParent().getUuid())
						.append("!sel\" z.type=\"Tcfc\">  </a>");
				}
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
			return null;
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
	 * <p>Deprecated since 2.4.1, due to too confusing.
	 * @deprecated
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
		sb.append("<img align=\"top\" z.fc=\"t\"");
			//z.fc used to let tree.js know what to clone
		HTMLs.appendAttribute(
			sb, "src", getDesktop().getExecution().encodeURL(uri));
		HTMLs.appendAttribute(sb, "width", width);
		HTMLs.appendAttribute(sb, "height", height);
		if (button) {
			final Treeitem item = getTreeitem();
			if (item != null) {
				sb.append(" z.type=\"Tcop\" id=\"")
					.append(item.getTreerow().getUuid()).append("!open\"");
			}

			//HTMLs.appendAttribute(sb, "title", title);
		}
		sb.append("/>");
	}

	/** Returns the width of the icon. */
	private int getIconWidth() {
		return getIntAttr("icon-width", 18);
	}
	/** Returns the height of the icon. */
	private int getIconHeight() {
		return getIntAttr("icon-height", 18);
	}
	/** Returns the icon URI.
	 */
	private String getIconURI(String name) {
		String s = Objects.toString(getAttribute("icon-uri"));
		if (s == null) s = "~./zul/img/tree";
		return s + name;
	}
	private int getIntAttr(String name, int defVal) {
		int v;
		Object o = getAttribute(name);
		if (o instanceof Number) {
			v = ((Number)o).intValue();
		} else {
			String s = Objects.toString(o);
			if (s == null || s.length() == 0)
				return defVal;

			v = Integer.parseInt(s);
			setAttribute(name, new Integer(v)); //to have better performance
		}
		return v > 0 ? v: defVal;
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
		final String attrs = super.getOuterAttrs();

		final Treecol col = getTreecol();
		final String clkattrs = getAllOnClickAttrs(false);
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
