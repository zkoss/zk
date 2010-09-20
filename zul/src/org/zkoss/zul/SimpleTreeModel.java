/* SimpleTreeModel.java

	Purpose:
		
	Description:
		
	History:
		Aug 10 2007, Created by Jeff Liu

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * A simple implementation of {@link TreeModel}.
 * Note: It assumes the content is immutable.
 *
 * @author Jeff Liu
 * @since 3.0.0
 */
public class SimpleTreeModel<E> extends AbstractTreeModel<SimpleTreeNode<E>> {
	
	/**
	 * Constructor
	 * @param root - the root of tree 
	 */
	public SimpleTreeModel(SimpleTreeNode<E> root) {
		super(root);
	}
	
	//--TreeModel--//
	public SimpleTreeNode<E> getChild(SimpleTreeNode<E> parent, int index) {
		return parent.getChildAt(index);
	}
	
	//--TreeModel--//
	public int getChildCount(SimpleTreeNode<E> parent) {
		return parent.getChildCount();
	}
	
	//--TreeModel--//
	public boolean isLeaf(SimpleTreeNode<E> node) {
		return node.isLeaf();
	}

}
