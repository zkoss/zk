/* Treecell.java

	Purpose:

	Description:

	History:
		Wed Jul  6 18:56:30     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;
import java.util.List;

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
 * <p>Default {@link #getZclass}: z-treecell (since 5.0.0)
 * @author tomyeh
 */
public class Treecell extends LabelImageElement {
	private AuxInfo _auxinf;

	public Treecell() {
	}

	public Treecell(String label) {
		super(label);
	}

	public Treecell(String label, String src) {
		super(label, src);
	}

	/** Return the tree that owns this cell.
	 */
	public Tree getTree() {
		for (Component n = this; (n = n.getParent()) != null;)
			if (n instanceof Tree)
				return (Tree) n;
		return null;
	}

	/** Returns the tree column associated with this cell, or null if not available.
	 */
	public Treecol getTreecol() {
		final Tree tree = getTree();
		if (tree != null) {
			final Treecols lcs = tree.getTreecols();
			if (lcs != null) {
				final int j = getColumnIndex();
				final List lcschs = lcs.getChildren();
				if (j < lcschs.size())
					return (Treecol) lcschs.get(j);
			}
		}
		return null;
	}

	/** Returns the column index of this cell, starting from 0.
	 */
	public int getColumnIndex() {
		int j = 0;
		for (Iterator it = getParent().getChildren().iterator(); it.hasNext(); ++j)
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
		return lc != null ? lc.getMaxlength() : 0;
	}

	/** Returns the level this cell is. The root is level 0.
	 */
	public int getLevel() {
		final Component parent = getParent();
		return parent != null ? ((Treerow) parent).getLevel() : 0;
	}

	/** Returns number of columns to span this cell.
	 * Default: 1.
	 */
	public int getSpan() {
		return _auxinf != null ? _auxinf.span : 1;
	}

	/** Sets the number of columns to span this cell.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span) {
		if (getSpan() != span) {
			initAuxInfo().span = span;
			smartUpdate("colspan", getSpan());
		}
	}

	public String getZclass() {
		return _zclass == null ? "z-treecell" : _zclass;
	}

	//Cloneable//
	public Object clone() {
		final Treecell clone = (Treecell) super.clone();
		if (_auxinf != null)
			clone._auxinf = (AuxInfo) _auxinf.clone();
		return clone;
	}

	//-- super --//
	/** Returns the width which the same as {@link #getTreecol}'s width.
	 */
	public String getWidth() {
		final Treecol col = getTreecol();
		return col != null ? col.getWidth() : null;
	}

	/**
	 * @deprecated as of release 6.0.0. To control the width of Treecell, please 
	 * use {@link Treecol#setWidth(String)} instead.
	 */
	public void setWidth(String width) {
		// Don't remove this method, it's to override super.setWidth().
	}

	/**
	 * @deprecated as of release 6.0.0. To control the hflex of Treecell, please 
	 * use {@link Treecol#setHflex(String)} instead.
	 */
	public void setHflex(String flex) {
		// Don't remove this method, it's to override super.setHflex().
	}

	//-- Component --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		if (getSpan() > 1)
			renderer.render("colspan", getSpan());
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Treerow))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}

	private AuxInfo initAuxInfo() {
		if (_auxinf == null)
			_auxinf = new AuxInfo();
		return _auxinf;
	}

	private static class AuxInfo implements java.io.Serializable, Cloneable {
		private int span = 1;

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
	}
}
