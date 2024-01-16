/* TreeEngine.java

        Purpose:

        Description:

        History:
                Tue Jan 09 17:09:45 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul.impl;

import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treeitem;

/**
 * A tree engine that handle ZK EE function of Tree component.
 * @author Jamson Chan
 * @since 10.0.0
 */
public interface TreeEngine {

	/** States of the tri-state selection. */
	enum State {
		SELECTED,
		UNSELECTED,
		PARTIAL
	}

	/**
	 * Sets the tree this engine belongs to.
	 */
	public void setTree(Tree tree);

	/**
	 * Returns the tree this engine belongs to.
	 */
	public Tree getTree();

	/** Sets whether the check mark shall be displayed in front
	 * of each item of the associated tree.
	 */
	public void setCheckmark(boolean checkmark);

	/** Returns whether the check mark shall be displayed in front
	 * of each item of the associated tree.
	 */
	public boolean isCheckmark();


	/** Sets whether multiple selections are allowed of the associated tree.
	 */
	public void setMultiple(boolean multiple);

	/** Returns whether multiple selections are allowed of the associated tree.
	 */
	public boolean isMultiple();

	/**
	 * Add the {@link EventListener} used for listen {@link SelectEvent} in tristate mode.
	 */
	public void addTristateOnSelectEventListener();

	/**
	 * Remove the {@link EventListener} used for listen {@link SelectEvent} in tristate mode.
	 */
	public void removeTristateOnSelectEventListener();

	/**
	 * Returns the {@link EventListener} used for listen {@link SelectEvent} in tristate mode.
	 */
	public EventListener<SelectEvent<Component, ?>> getTristateOnSelectListener();

	/** Add item to partial selected set.
	 */
	public void addItemToPartial(Treeitem item);

	/** Remove item from partial selected set.
	 */
	public void removeItemFromPartial(Treeitem item);

	/** Initialize partial selected set.
	 */
	public void initPartialSet();

	/** Clear partial selected set.
	 */
	public void clearPartialSet();

	/** Returns whether the partial selected set is empty.
	 */
	public boolean isPartialEmpty();

	/** Returns all partial selected set.
	 */
	public Set<Treeitem> getPartialItems();

	/** Returns the count of partial selected set.
	 */
	public int getPartialCount();

	/** Update descendant {@link Treeitem}'s tristate.
	 * <p>If you modify this method, please also modify the
	 * {@link TreeNode} version of this method {@link #updateDescendantsNodesTristate}.
	 */
	public void updateDescendantsItemsTristate(Treeitem item, boolean isSelected);

	/** Update ancestor {@link Treeitem}'s tristate.
	 * <p>If you modify this method, please also modify the
	 * {@link TreeNode} version of this method {@link #updateAncestorsNodesTristate}.
	 */
	public void updateAncestorsItemsTristate(Treeitem item);

	/** Update descendant {@link TreeNode}'s tristate.
	 * <p>If you modify this method, please also modify the
	 * {@link Treeitem} version of this method {@link #updateDescendantsItemsTristate}.
	 */
	public void updateDescendantsNodesTristate(TreeNode node, boolean isSelected);

	/** Update ancestor {@link TreeNode}'s tristate.
	 * <p>If you modify this method, please also modify the
	 * {@link Treeitem} version of this method {@link #updateAncestorsItemsTristate}.
	 */
	public void updateAncestorsNodesTristate(TreeNode node);

	/** Update header checkmark's tristate depend on root's children.
	 */
	public void updateHeadercmTristate();

	/** Update header checkmark's tristate directly by given state.
	 */
	public void updateHeadercmTristateDirectly(State state);

	/** A recursive method for initializing {@link Treeitem} when changed to tristate mode,
	 * <p>Returns the selection state of current {@link Treeitem}.
	 * <p>If you modify this method, please also modify the TreeNode version of this method {@link #initNodesTristate}
	 * @param comp might be {@link Tree} (root) or {@link Treeitem} (others).
	 */
	public State initItemsTristate(Component comp);

	/** A recursive method for initializing treeNodes when changed to tristate mode,
	 * <p>Returns the selection state of current {@link TreeNode}.
	 * <p>If you modify this method, please also modify the Treeitem version of this method {@link #initItemsTristate}
	 */
	public State initNodesTristate(TreeNode cur);

	/** Returns whether the tree model is a Tristate model.
	 */
	public boolean isTristateModel();
}
