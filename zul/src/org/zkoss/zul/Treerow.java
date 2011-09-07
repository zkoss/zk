/* Treerow.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:56:22     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.XulElement;

/**
 * A treerow.
 * <p>Default {@link #getZclass}: z-treerow (since 5.0.0)
 * @author tomyeh
 */
public class Treerow extends XulElement {
	public Treerow() {
	}
	/** Instantiates a treerow with a treecel holding the given label.
	 * @since 5.0.8
	 */
	public Treerow(String label) {
		setLabel(label);
	}

	/** Returns the {@link Tree} instance containing this element.
	 */
	public Tree getTree() {
		for (Component p = this; (p = p.getParent()) != null;)
			if (p instanceof Tree)
				return (Tree)p;
		return null;
	}

	/** Returns the level this cell is. The root is level 0.
	 */
	public int getLevel() {
		final Component parent = getParent();
		return parent != null ? ((Treeitem)parent).getLevel(): 0;
	}

	/** Returns the {@link Treechildren} associated with this
	 * {@link Treerow}.
	 * In other words, it is {@link Treeitem#getTreechildren} of
	 * {@link #getParent}.
	 * @since 2.4.1
	 * @see Treechildren#getLinkedTreerow
	 */
	public Treechildren getLinkedTreechildren() {
		final Component parent = getParent();
		return parent != null ? ((Treeitem)parent).getTreechildren(): null;
	}

	/** Returns the label of the {@link Treecell} it contains, or null
	 * if no such cell.
	 * @since 5.0.8
	 */
	public String getLabel() {
		final Treecell cell = (Treecell)getFirstChild();
		return cell != null ? cell.getLabel(): null;
	}
	/** Sets the label of the {@link Treecell} it contains.
	 *
	 * <p>If treecell are not created, we automatically create it.
	 *
	 * <p>Notice that this method will create a treecell automatically
	 * if they don't exist.
	 * @since 5.0.8
	 */
	public void setLabel(String label) {
		autoFirstCell().setLabel(label);
	}
	/** Returns the image of the {@link Treecell} it contains, or null
	 * if no such cell.
	 * @since  5.0.8
	 */
	public String getImage() {
		final Treecell cell = (Treecell)getFirstChild();
		return cell != null ? cell.getImage(): null;
	}
	/** Sets the image of the {@link Treecell} it contains.
	 *
	 * <p>If treecell are not created, we automatically create it.
	 *
	 * <p>Notice that this method will create a treerow and treecell automatically
	 * if they don't exist.
	 * @since 5.0.8
	 */
	public void setImage(String image) {
		autoFirstCell().setImage(image);
	}
	private Treecell autoFirstCell() {
		Treecell cell = (Treecell)getFirstChild();
		if (cell == null) {
			cell = new Treecell();
			cell.applyProperties();
			cell.setParent(this);
		}
		return cell;
	}

	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-treerow" : _zclass;
	}
	public void smartUpdate(String attr, Object value) {
		super.smartUpdate(attr, value);
	}
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Treeitem))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Treecell))
			throw new UiException("Unsupported child for tree row: "+child);
		super.beforeChildAdded(child, refChild);
	}}
