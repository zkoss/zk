/* DefaultTreeModel.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan  5 17:37:01 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.DefaultTreeNode.TreeNodeChildrenList;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.ext.Sortable;

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
 * @author jumperchen
 * @since 5.0.6
 */
public class DefaultTreeModel<E> extends AbstractTreeModel<TreeNode<E>>
		implements Sortable<TreeNode<E>>, java.io.Serializable {

	private static final long serialVersionUID = 20110131094811L;

	private Comparator<TreeNode<E>> _sorting;
	private boolean _sortDir;
	/** Whether to treat the zero size of children node as a leaf node. */
	private boolean _emptyleaf;

	/** Creates a tree with the specified note as the root.
	 * @param root the root (cannot be null).
	 */
	public DefaultTreeModel(TreeNode<E> root) {
		this(root, false);
	}

	/** Creates a tree with the specified note as the root.
	 * @param root the root (cannot be null).
	 * @param emptyChildAsLeaf whether to treat the zero size of children node as a leaf node.
	 * @since 6.0.3
	 */
	public DefaultTreeModel(TreeNode<E> root, boolean emptyChildAsLeaf) {
		super(root);
		TreeNode<E> parent = root.getParent();
		if (parent != null)
			parent.remove(root);
		root.setModel(this);
		_emptyleaf = emptyChildAsLeaf;
	}

	public boolean isLeaf(TreeNode<E> node) {
		boolean isLeaf = node.isLeaf();
		if (!isLeaf && _emptyleaf)
			return node.getChildCount() == 0;
		return isLeaf;
	}

	public TreeNode<E> getChild(TreeNode<E> parent, int index) {
		return parent.getChildAt(index);
	}

	public int getChildCount(TreeNode<E> parent) {
		return parent.getChildCount();
	}

	public int getIndexOfChild(TreeNode<E> parent, TreeNode<E> child) {
		return parent.getIndex(child);
	}

	/**
	 * Returns the path from the child, where the path indicates the child is
	 * placed in the whole tree.
	 * @param child the node we are interested in
	 * @since 6.0.0
	 */
	public int[] getPath(TreeNode<E> child) {
		final TreeNode<E> root = getRoot();
		List<Integer> p = new ArrayList<Integer>();
		while (root != child) {
			TreeNode<E> parent = child.getParent();
			if (parent != null) {
				for (int i = 0, j = parent.getChildCount(); i < j; i++) {
					if (parent.getChildAt(i) == child) {
						p.add(0, i);
						break;
					}
				}
				child = parent;
			} else
				break; // ZK-838
		}
		final Integer[] objs = p.toArray(new Integer[p.size()]);
		final int[] path = new int[objs.length];
		for (int i = 0; i < objs.length; i++)
			path[i] = objs[i].intValue();
		return path;
	}

	//Selectable//
	public boolean isSelected(Object child) {
		return child instanceof TreeNode && super.isSelected(child);
	}

	public boolean removeFromSelection(Object child) {
		return child instanceof TreeNode && super.removeFromSelection(child);
	}

	//Openable//
	public boolean isObjectOpened(Object child) {
		return child instanceof TreeNode && super.isObjectOpened(child);
	}

	public boolean removeOpenObject(Object child) {
		return child instanceof TreeNode && super.removeOpenObject(child);
	}

	//-- Sortable --//
	/** Sorts the data.
	 *
	 * <p>Notice: it invokes {@link #beforeSort} and {@link #afterSort}
	 * to save and restore the selection and open states.
	 * If you prefer not to preserve objects and prefer to save the paths,
	 * you can override {@link #beforeSort} to do nothing but returning null.
	 * If you prefer to clear the selection, you can override {@link #beforeSort}
	 * to clear {@link #_selection} and return null.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmprt to compare.
	 */
	public void sort(Comparator<TreeNode<E>> cmpr, final boolean ascending) {
		_sorting = cmpr;
		_sortDir = ascending;
		TreeNode<E> root = getRoot();
		if (root != null) {
			final Object ctx = beforeSort();
			sort0(root, cmpr);
			afterSort(ctx); //before firing event
			fireEvent(TreeDataEvent.STRUCTURE_CHANGED, null, 0, 0);
		}
	}

	public void sort() {
		if (_sorting == null)
			throw new UiException("The sorting comparator is not assigned, please use sort(Comparator cmpr, final boolean ascending)");
		sort(_sorting, _sortDir);
	}

	@SuppressWarnings("rawtypes")
	private void sort0(TreeNode<E> node, Comparator<TreeNode<E>> cmpr) {
		if (node.getChildren() == null)
			return;
		if (node instanceof DefaultTreeNode)
			((TreeNodeChildrenList) node.getChildren()).treeSort(cmpr);
		else
			Collections.sort(node.getChildren(), cmpr);
		for (TreeNode<E> child : node.getChildren())
			sort0(child, cmpr);
	}

	public String getSortDirection(Comparator<TreeNode<E>> cmpr) {
		if (Objects.equals(_sorting, cmpr))
			return _sortDir ? "ascending" : "descending";
		return "natural";
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		final DefaultTreeModel clone = (DefaultTreeModel) super.clone();
		final TreeNode cloneRoot = (TreeNode) getRoot().clone();
		cloneRoot.setModel(this);
		clone.setRootDirectly(cloneRoot);
		return clone;
	}
}
