/* Treecols.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:55:52     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.HeadersElement;

/**
 * A treecols.
 * <p>Default {@link #getZclass}: z-tree-cols.(since 3.5.0)
 * @author tomyeh
 */
public class Treecols extends HeadersElement implements org.zkoss.zul.api.Treecols {
	/** Returns the tree that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Tree getTree() {
		return (Tree)getParent();
	}
	/** Returns the tree that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tree getTreeApi() {
		return getTree();
	}

	//super//
	public boolean setVisible(boolean visible) {
		final boolean vis = super.setVisible(visible);
		final Tree tree = getTree();
		if (tree != null)
			tree.invalidate();
		return vis;
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());
		final Tree tree = getTree();
		if (tree != null)
			HTMLs.appendAttribute(sb, "z.rid", tree.getUuid());
		return sb.toString();
	}

	public String getZclass() {
		return _zclass == null ? "z-tree-cols" : _zclass;
	}
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Tree))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Treecol))
			throw new UiException("Unsupported child for treecols: "+child);
		return super.insertBefore(child, insertBefore);
	}
}
