package org.zkoss.clientbind.test.mvvm.book.comp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

public class Tree {
	DefaultTreeModel<String> treeModel = new DefaultTreeModel<String>(new DefaultTreeNode<String>("Root", new DefaultTreeNode[]{new DefaultTreeNode<String>("Root.0", new DefaultTreeNode[]{new DefaultTreeNode<String>("Root.0.0"), new DefaultTreeNode<String>("Root.0.1")}), new DefaultTreeNode<String>("Root.1", new DefaultTreeNode[]{new DefaultTreeNode<String>("Root.1.0"), new DefaultTreeNode<String>("Root.1.1")})}));

	private TreeNode<String> selected = new DefaultTreeNode<String>("init");

	private List<TreeNode<String>> selectedItems = new ArrayList<>();
	private boolean open = false;

	public TreeNode<String> getSelected() {
		return selected;
	}

	public void setSelected(TreeNode<String> selected) {
		this.selected = selected;
	}

	public List<TreeNode<String>> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<TreeNode<String>> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public TreeModel getModel() {
		return treeModel;
	}

	// -----------command -----------------
	@Command
	@NotifyChange("*")
	public void open() {
		open = true;
	}

	@Command
	@NotifyChange("selected")
	public void doSelectFirst() {
		int[] child = {0};
		selected = treeModel.getChild(child);
	}

	@Command
	@NotifyChange("selectedItems")
	public void doSelectFirstLayer() {
		int[] child = {0};
		selectedItems.add(treeModel.getChild(new int[]{0}));
		selectedItems.add(treeModel.getChild(new int[]{1}));
	}
}
