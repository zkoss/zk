/* Treecol.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:55:59     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.Iterator;

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.HeaderElement;

/**
 * A treecol.
 *
 * @author tomyeh
 */
public class Treecol extends HeaderElement {
	private int _maxlength;

	public Treecol() {
	}
	public Treecol(String label) {
		setLabel(label);
	}
	/* Constructs a tree header with label and image.
	 *
	 * @param lable the label. No label if null or empty.
	 * @param src the URI of the image. Ignored if null or empty.
	 */
	public Treecol(String label, String src) {
		setLabel(label);
		setSrc(src);
	}
	/* Constructs a tree header with label, image and width.
	 *
	 * @param src the URI of the image. Ignored if null or empty.
	 * @param width the width of the column. Ignored if null or empty.
	 * @since 3.0.4
	 */
	public Treecol(String label, String src, String width) {
		setLabel(label);
		setSrc(src);
		setWidth(width);
	}

	/** Returns the tree that it belongs to.
	 */
	public Tree getTree() {
		final Component comp = getParent();
		return comp != null ? (Tree)comp.getParent(): null;
	}
	
	/** Returns the maximal length of each item's label.
	 */
	public int getMaxlength() {
		return _maxlength;
	}
	/** Sets the maximal length of each item's label.
	 */
	public void setMaxlength(int maxlength) {
		if (maxlength < 0) maxlength = 0;
		if (_maxlength != maxlength) {
			_maxlength = maxlength;
			invalidateCells();
		}
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

	/** Invalidates the relevant cells. */
	private void invalidateCells() {
		final Tree tree = getTree();
		if (tree != null)
			invalidateCells(tree.getTreechildren(), getColumnIndex());
	}
	private static void invalidateCells(Treechildren tch, int jcol) {
		if (tch == null)
			return;

		for (Iterator it = tch.getChildren().iterator(); it.hasNext();) {
			final Treeitem ti = (Treeitem)it.next();
			final Treerow tr = ti.getTreerow();
			if (tr != null) {
				final List chs = tr.getChildren();
				if (jcol < chs.size())
					((Component)chs.get(jcol)).invalidate();
			}

			invalidateCells(ti.getTreechildren(), jcol); //recursive
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String clkattrs = getAllOnClickAttrs();
		return clkattrs == null ? attrs: attrs + clkattrs;
	}
	/** Invalidates the whole tree. */
	protected void invalidateWhole() {
		final Tree tree = getTree();
		if (tree != null) tree.invalidate();
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Treecols))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
}
