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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.HeaderElement;

/**
 * A treecol.
 * <p>Default {@link #getZclass}: z-treecol (since 5.0.0)
 * @author tomyeh
 */
public class Treecol extends HeaderElement implements org.zkoss.zul.api.Treecol {
	private int _maxlength;

	public Treecol() {
	}
	public Treecol(String label) {
		super(label);
	}
	/* Constructs a tree header with label and image.
	 *
	 * @param lable the label. No label if null or empty.
	 * @param src the URI of the image. Ignored if null or empty.
	 */
	public Treecol(String label, String src) {
		super(label, src);
	}
	/* Constructs a tree header with label, image and width.
	 *
	 * @param src the URI of the image. Ignored if null or empty.
	 * @param width the width of the column. Ignored if null or empty.
	 * @since 3.0.4
	 */
	public Treecol(String label, String src, String width) {
		setLabel(label);
		setImage(src);
		setWidth(width);
	}

	/** Returns the tree that it belongs to.
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
			smartUpdate("maxlength", maxlength);
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

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-treecol" : _zclass;
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Treecols))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}
}
