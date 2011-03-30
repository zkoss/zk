/* DefaultTreeModel.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan  5 17:37:01 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.zkoss.zul.event.TreeDataEvent;

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
 *
 * <p>For introduction, please refer to
 * <a href="http://books.zkoss.org/wiki/ZK_Developer's_Reference/MVC/Model/Tree_Model">ZK Developer's Reference: Tree Model</a>.
 *
 * @author tomyeh
 * @since 5.0.6
 */
public class DefaultTreeModel<E> extends AbstractTreeModel<TreeNode<E>>
implements TreeModelExt<TreeNode<E>>, java.io.Serializable {

	private static final long serialVersionUID = 20110131094811L;

	/** Creates a tree with the specified note as the root.
	 * @param root the root (cannot be null).
	 */
	public DefaultTreeModel(TreeNode<E> root) {
		super(root);

		TreeNode<E> parent = root.getParent();
		if (parent != null)
			parent.remove(root);
		root.setModel(this);
	}

	//@Override
	public boolean isLeaf(TreeNode<E> node) {
		return node.isLeaf();
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
	
	//-- TreeModelExt --//
	/** Sorts the data.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmprt to compare.
	 */
	public void sort(Comparator<TreeNode<E>> cmpr, final boolean ascending) {
		TreeNode<E> root = getRoot();
		if (root != null) {
			sort0(root, cmpr);
			fireStructureChangedEvent(root);
		}
	}
	
	private void sort0(TreeNode<E> node, Comparator<TreeNode<E>> cmpr) {
		if (node.getChildren() == null) return;
		Collections.sort(node.getChildren(), cmpr);
		for (TreeNode<E> child: node.getChildren())
			sort0(child, cmpr);
	}
	
	private void fireStructureChangedEvent(TreeNode<E> node) {
		if (node.getChildCount() == 0) return;
		fireEvent(node,  0, 0,TreeDataEvent.STRUCTURE_CHANGED);
	}
}
