/* Treefooter.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 19 15:36:11     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.Iterator;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * A column of the footer of a tree ({@link Tree}).
 * Its parent must be {@link Treefoot}.
 *
 * <p>Unlike {@link Treecol}, you could place any child in a tree footer.
 * <p>Note: {@link Treecell} also accepts children.
 * <p>Default {@link #getZclass}: z-treefooter (since 5.0.0)
 * 
 * @author tomyeh
 */
public class Treefooter extends LabelImageElement implements org.zkoss.zul.api.Treefooter {
	private int _span = 1;

	public Treefooter() {
	}
	public Treefooter(String label) {
		super(label);
	}
	public Treefooter(String label, String src) {
		super(label, src);
	}

	/** Returns the tree that this belongs to.
	 */
	public Tree getTree() {
		final Component comp = getParent();
		return comp != null ? (Tree)comp.getParent(): null;
	}
	/** Returns the tree that it belongs to.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tree getTreeApi() {
		return getTree();
	}
	/** Returns the column index, starting from 0.
	 */
	public int getColumnIndex() {
		int j = 0;
		for (Iterator it = getParent().getChildren().iterator();
		it.hasNext(); ++j)
			if (it.next() == this)
				break;
		return j;
	}
	/** Returns the tree header that is in the same column as
	 * this footer, or null if not available.
	 */
	public Treecol getTreecol() {
		final Tree tree = getTree();
		if (tree != null) {
			final Treecols cs = tree.getTreecols();
			if (cs != null) {
				final int j = getColumnIndex();
				final List cschs = cs.getChildren();
				if (j < cschs.size())
					return (Treecol)cschs.get(j);
			}
		}
		return null;
	}
	/** Returns the tree header that is in the same column as
	 * this footer, or null if not available.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Treecol getTreecolApi() {
		return getTreecol();
	}

	/** Returns number of columns to span this footer.
	 * Default: 1.
	 */
	public int getSpan() {
		return _span;
	}
	/** Sets the number of columns to span this footer.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span) {
		if (_span != span) {
			_span = span;
			smartUpdate("colspan", _span);
		}
	}

	//-- super --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		
		if (_span > 1)
			renderer.render("colspan", _span);

		org.zkoss.zul.impl.Utils.renderCrawlableText(getLabel());
	}
	
	public String getZclass() {
		return _zclass == null ? "z-treefooter" : _zclass;
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Treefoot))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}
}
