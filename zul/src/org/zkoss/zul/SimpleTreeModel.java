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
public class SimpleTreeModel<E> extends AbstractTreeModel<TreeNode<E>> {
	/**
	 * Constructor
	 * @param root - the root of tree 
	 */
	public SimpleTreeModel(TreeNode<E> root) {
		super(root);
	}
	
	//@Override
	public TreeNode<E> getChild(TreeNode<E> parent, int index) {
		return parent.getChildAt(index);
	}
	
	//@Override
	public int getChildCount(TreeNode<E> parent) {
		return parent.getChildCount();
	}

	//@Override
	public int getIndexOfChild(TreeNode<E> parent, TreeNode<E> child) {
		return parent.getIndex(child);
	}
	
	//@Override
	public boolean isLeaf(TreeNode<E> node) {
		return node.isLeaf();
	}
}
