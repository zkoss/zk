/* DefaultTreeModel.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan  5 17:37:01 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul;

/**
 * A simple tree data model that uses {@link TreeNode} to represent a tree.
 * Thus the whole tree of data must be loaded into memory, and each node
 * must be represented by {@link TreeNode}.
 *
 * <p>If you want to implement a huge tree that only a visible part shall
 * be loaded, it is better to implement it by extending from
 * {@link AbstractTreeModel}.
 *
 * <p>{@link DefaultTreeModel} depends on {@link TreeNode} only.
 * It does not depend on {@link DefaultTreeNode}. However, {@link DefaultTreeNode}
 * depends on {@link DefaultTreeModel}.
 * @author tomyeh
 * @since 5.0.6
 */
public class DefaultTreeModel extends AbstractTreeModel
implements java.io.Serializable {

	/** Creates a tree with the specified note as the root.
	 * @param root the root (cannot be null).
	 */
	public DefaultTreeModel(TreeNode root) {
		super(root);
		TreeNode parent = root.getParent();
		if (parent != null)
			parent.remove(root);
		root.setModel(this);
	}

	//@Override
	public boolean isLeaf(Object node) {
		return ((TreeNode)node).isLeaf();
	}
	//@Override
	public Object getChild(Object parent, int index) {
		return ((TreeNode)parent).getChildAt(index);
	}
	//@Override
	public int getChildCount(Object parent) {
		return ((TreeNode)parent).getChildCount();
	}
	//@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((TreeNode)parent).getIndex((TreeNode)child);
	}
}
