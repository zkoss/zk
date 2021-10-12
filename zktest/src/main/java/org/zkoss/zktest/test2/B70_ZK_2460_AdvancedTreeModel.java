package org.zkoss.zktest.test2;

import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

public class B70_ZK_2460_AdvancedTreeModel extends DefaultTreeModel<B70_ZK_2460_Contact> {
	private static final long serialVersionUID = -5513180500300189445L;
	
	DefaultTreeNode<B70_ZK_2460_Contact> _root;

	public B70_ZK_2460_AdvancedTreeModel(B70_ZK_2460_ContactTreeNode B70_ZK_2460_B70_ZK_2460_ContactTreeNode) {
		super(B70_ZK_2460_B70_ZK_2460_ContactTreeNode);
		_root = B70_ZK_2460_B70_ZK_2460_ContactTreeNode;
	}

	/**
	 * remove the nodes which parent is <code>parent</code> with indexes
	 * <code>indexes</code>
	 * 
	 * @param parent
	 *            The parent of nodes are removed
	 * @param indexFrom
	 *            the lower index of the change range
	 * @param indexTo
	 *            the upper index of the change range
	 * @throws IndexOutOfBoundsException
	 *             - indexFrom < 0 or indexTo > number of parent's children
	 */
	public void remove(DefaultTreeNode<B70_ZK_2460_Contact> parent, int indexFrom, int indexTo) throws IndexOutOfBoundsException {
		DefaultTreeNode<B70_ZK_2460_Contact> stn = parent;
		for (int i = indexTo; i >= indexFrom; i--)
			try {
				stn.getChildren().remove(i);
			} catch (Exception exp) {
				exp.printStackTrace();
			}
	}

	public void remove(DefaultTreeNode<B70_ZK_2460_Contact> target) throws IndexOutOfBoundsException {
		int index = 0;
		DefaultTreeNode<B70_ZK_2460_Contact> parent = null;
		// find the parent and index of target
		parent = dfSearchParent(_root, target);
		for (index = 0; index < parent.getChildCount(); index++) {
			if (parent.getChildAt(index).equals(target)) {
				break;
			}
		}
		remove(parent, index, index);
	}

	/**
	 * insert new nodes which parent is <code>parent</code> with indexes
	 * <code>indexes</code> by new nodes <code>newNodes</code>
	 * 
	 * @param parent
	 *            The parent of nodes are inserted
	 * @param indexFrom
	 *            the lower index of the change range
	 * @param indexTo
	 *            the upper index of the change range
	 * @param draggedValue
	 *            New nodes which are inserted
	 * @throws IndexOutOfBoundsException
	 *             - indexFrom < 0 or indexTo > number of parent's children
	 */
	public void insert(DefaultTreeNode<B70_ZK_2460_Contact> parent, int indexFrom, int indexTo, DefaultTreeNode<B70_ZK_2460_Contact>  ... draggedValue)
			throws IndexOutOfBoundsException {
		DefaultTreeNode<B70_ZK_2460_Contact> stn = parent;
		for (int i = indexFrom; i <= indexTo; i++) {
			try {
				stn.getChildren().add(i, draggedValue[i - indexFrom]);
			} catch (Exception exp) {
				throw new IndexOutOfBoundsException("Out of bound: " + i + " while size=" + stn.getChildren().size());
			}
		}
	}

	/**
	 * append new nodes which parent is <code>parent</code> by new nodes
	 * <code>newNodes</code>
	 * 
	 * @param parent
	 *            The parent of nodes are appended
	 * @param newNodes
	 *            New nodes which are appended
	 */
	public void add(DefaultTreeNode<B70_ZK_2460_Contact> parent, DefaultTreeNode<B70_ZK_2460_Contact>... newNodes) {
		DefaultTreeNode<B70_ZK_2460_Contact> stn = (DefaultTreeNode<B70_ZK_2460_Contact>) parent;
		for (int i = 0; i < newNodes.length; i++)
			stn.getChildren().add(newNodes[i]);

	}

	private DefaultTreeNode<B70_ZK_2460_Contact> dfSearchParent(DefaultTreeNode<B70_ZK_2460_Contact> node, DefaultTreeNode<B70_ZK_2460_Contact> target) {
		if (node.getChildren() != null && node.getChildren().contains(target)) {
			return node;
		} else {
			int size = getChildCount(node);
			for (int i = 0; i < size; i++) {
				DefaultTreeNode<B70_ZK_2460_Contact> parent = dfSearchParent((DefaultTreeNode<B70_ZK_2460_Contact>) getChild(node, i), target);
				if (parent != null) {
					return parent;
				}
			}
		}
		return null;
	}

}
