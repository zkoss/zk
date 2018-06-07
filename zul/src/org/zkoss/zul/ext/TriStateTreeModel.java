/* TriStateTreeModel.java

        Purpose:
                
        Description:
                
        History:
                Thu Jun 07 16:07:43 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zul.ext;

import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeNode;

/**
 * This interface defines the methods that component like {@link Tree}
 * use tri state (FULL, PARTIAL and NONE) for checkbox in the component.
 * 
 * @author klyvechen
 * @since 8.5.2
 */
public interface TriStateTreeModel<E> {
	/**
	 * update selection status of a node's subtree.
	 * Tree already add the selected treenode into the selection, we only add its sub-nodes 
	 * @param node
	 * @param selected
	 */
	public void toggleSubtree(TreeNode<E> node, final boolean selected);

	/**
	 *  update selection status of a node's ancestors / i.e. make them none/partial/full and automatically add/ remove them from selection
	 * @param node
	 * @param selected
	 */
	public void toggleAncestors(TreeNode<E> node, boolean selected);

	/**
	 * A method to calculate the selection state of the tree
	 * @param node
	 * @return selection state
	 */
	public SelectionState calculateSelectionState(TreeNode<E> node);

	/**
	 * Return the selection state of the tree
	 * @param node
	 * @return selection state
	 */
	public SelectionState getSelectionState(TreeNode<E> node);

	enum SelectionState {
		FULL, 
		PARTIAL, 
		NONE;
	}
}