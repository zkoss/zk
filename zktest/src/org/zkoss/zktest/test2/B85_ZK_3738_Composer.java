/* B85_ZK_3738_Composer.java

        Purpose:
                
        Description:
                
        History:
                Thu Jan 25 12:07 PM:15 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

public class B85_ZK_3738_Composer extends SelectorComposer<Component> {
	private final List<TreeNode<String>> allTreeNodes = new ArrayList<TreeNode<String>>();
	private final DefaultTreeModel<String> treeModel;

	@Wire("Label#l1")
	Label l1;

	public B85_ZK_3738_Composer() {
		TreeNode<String> root = treeNode("root",
				treeNode("1",
						treeNode("1.1"),
						treeNode("1.2")),
				treeNode("2",
						treeNode("2.1"),
						treeNode("2.2"),
						treeNode("2.3")),
				treeNode("3",
						treeNode("3.1")));
		treeModel = new DefaultTreeModel<String>(root);
		treeModel.setMultiple(true);
	}

	public TreeModel<TreeNode<String>> getTreeModel() {
		return treeModel;
	}

	@Listen("onSelect=tree")
	public void onSelect(SelectEvent<Tree, TreeNode<String>> ev) {
		Clients.log("previous: " + ev.getPreviousSelectedObjects());
		Clients.log("current from event: " + ev.getSelectedObjects());
		Clients.log("unselected from event: " + ev.getUnselectedObjects());
		Clients.log("current from model: " + treeModel.getSelection());
		l1.setValue(ev.getSelectedObjects().toString());
	}

	@Listen("onClick=button#selectAll")
	public void onSelectAll(Event ev) {
		treeModel.setSelection(allTreeNodes);
	}

	private final TreeNode<String> treeNode(String data) {
		DefaultTreeNode<String> treeLeaf = new DefaultTreeNode<String>(data);
		allTreeNodes.add(treeLeaf);
		return treeLeaf;
	}

	private final TreeNode<String> treeNode(String data, TreeNode<String>... children) {
		DefaultTreeNode<String> treeNode = new DefaultTreeNode<String>(data, Arrays.asList(children));
		allTreeNodes.add(treeNode);
		return treeNode;
	}
}

