/* EmptyTreeEngine.java

        Purpose:
                
        Description:
                
        History:
                Wed Jan 10 11:43:51 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul.impl;

import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treeitem;

/**
 * An empty tree engine that implement {@link TreeEngine}.
 * @author Jamson Chan
 * @since 10.0.0
 */
public class EmptyTreeEngine implements TreeEngine {

	public void setTree(Tree tree) {

	}

	public Tree getTree() {
		return null;
	}

	public void setModel(TreeModel<?> model) {

	}

	public TreeModel<?> getModel() {
		return null;
	}

	public void setCheckmark(boolean checkmark) {

	}

	public boolean isCheckmark() {
		return false;
	}

	public void setMultiple(boolean multiple) {

	}

	public boolean isMultiple() {
		return false;
	}

	public void addTristateOnSelectEventListener() {

	}

	public void removeTristateOnSelectEventListener() {

	}

	public EventListener<SelectEvent<Component, ?>> getTristateOnSelectListener() {
		return null;
	}

	public void addItemToPartial(Treeitem item) {

	}

	public void removeItemFromPartial(Treeitem item) {

	}

	public void initPartialSet() {

	}

	public void clearPartialSet() {

	}

	public boolean isPartialEmpty() {
		return true;
	}

	public Set<Treeitem> getPartialItems() {
		return null;
	}

	public int getPartialCount() {
		return 0;
	}

	public void updateDescendantsItemsTristate(Treeitem item, boolean isSelected) {

	}

	public void updateAncestorsItemsTristate(Treeitem item) {

	}

	public void updateDescendantsNodesTristate(TreeNode node, boolean isSelected) {

	}

	public void updateAncestorsNodesTristate(TreeNode node) {

	}

	public void updateHeadercmTristate() {

	}

	public void updateHeadercmTristateDirectly(int state) {

	}

	public int initItemsTristate(Component comp) {
		return 0;
	}

	public int initNodesTristate(TreeNode cur) {
		return 0;
	}

	public boolean isTristateModel() {
		return false;
	}

}
