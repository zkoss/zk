/* Treecol.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:55:59     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.List;
import java.util.Iterator;

import com.potix.lang.Objects;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

import com.potix.zul.html.impl.HeaderElement;

/**
 * A treecol.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.8 $ $Date: 2006/05/29 04:28:28 $
 */
public class Treecol extends HeaderElement {
	private int _maxlength;

	public Treecol() {
	}
	public Treecol(String label) {
		setLabel(label);
	}
	public Treecol(String label, String src) {
		setLabel(label);
		setSrc(src);
	}

	/** Returns the tree that it belongs to.
	 */
	public Tree getTree() {
		final Component comp = getParent();
		return comp != null ? (Tree)comp.getParent(): null;
	}

	/** Returns the maximal length of each item's label.
	 * <p>Note: DBCS counts  two bytes (range 0x4E00~0x9FF).
	 * Default: 0 (no limit).
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
			invalidateCells(INNER);
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
	private void invalidateCells(Range range) {
		final Tree tree = getTree();
		if (tree != null)
			invalidateCells(tree.getTreechildren(), getColumnIndex(), range);
	}
	private static void invalidateCells(Treechildren tch, int jcol, Range range) {
		if (tch == null)
			return;

		for (Iterator it = tch.getChildren().iterator(); it.hasNext();) {
			final Treeitem ti = (Treeitem)it.next();
			final Treerow tr = ti.getTreerow();
			if (tr != null) {
				final List chs = tr.getChildren();
				if (jcol < chs.size())
					((Component)chs.get(jcol)).invalidate(range);
			}

			invalidateCells(ti.getTreechildren(), jcol, range); //recursive
		}
	}

	//-- super --//
	/** Invalidates the whole tree. */
	protected void invalidateWhole() {
		final Tree tree = getTree();
		if (tree != null) tree.invalidate(INNER);
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Treecols))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public void invalidate(Range range) {
		super.invalidate(range);
		initAtClient();
	}
	private void initAtClient() {
		final Tree tree = getTree();
		if (tree != null) tree.initAtClient();
	}
}
