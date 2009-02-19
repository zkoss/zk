/* Treefoot.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 19 15:36:05     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.XulElement;

/**
 * A row of {@link Treefooter}.
 *
 * <p>Like {@link Treecols}, each tree has at most one {@link Treefoot}.
 * <p>Default {@link #getZclass}: z-tree-foot.(since 3.5.0)
 * @author tomyeh
 */
public class Treefoot extends XulElement implements org.zkoss.zul.api.Treefoot {
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
	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-tree-foot" : _zclass;
	}
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Tree))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Treefooter))
			throw new UiException("Unsupported child for treefoot: "+child);
		return super.insertBefore(child, insertBefore);
	}
}
