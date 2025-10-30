/* B80_ZK_3022Test.java

	Purpose:
		
	Description:
		
	History:
		Tue, Jan  5, 2016  6:22:27 PM, Created by Christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

/**
 * 
 * @author Christopher
 */
public class B80_ZK_3022Composer extends SelectorComposer<Window>{

	private Tree expandAllTree;
	private DefaultTreeModel<String> model;
	
	@Wire
	private Button btn;
	
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		
		// build tree when init page
		DefaultTreeNode<String> rootNode = new DefaultTreeNode<String>("root", new ArrayList<DefaultTreeNode<String>>());

		addChildNode(rootNode, 1, 1);

		for (TreeNode<String> nodeLv1 : rootNode.getChildren()) {
			addChildNode(nodeLv1, 2, 1);

			for (TreeNode<String> nodeLv2 : nodeLv1.getChildren()) {
				addChildNode(nodeLv2, 3, 1);
			}
		}

		model = new DefaultTreeModel<String>(rootNode);
		
		expandAllTree = new Tree();
		expandAllTree.setModel(model);
		comp.appendChild(expandAllTree);
	}

	@Listen("onClick=#btn")
	public void click() {
		collapseTree(expandAllTree, true);
	}
	
	// function add child node
	public void addChildNode(TreeNode<String> node, int level, int num) {
		for (int i = 0; i < num; i++) {
			DefaultTreeNode<String> child = new DefaultTreeNode<String>(String.valueOf(level) + "_" + String.valueOf(i),
					new ArrayList<DefaultTreeNode<String>>());
			node.add(child);
		}
	}

	// function to expand tree
	protected void collapseTree(Component treeObject, boolean isOpen) {
		if (treeObject instanceof Treeitem) {
			Treeitem treeitem = (Treeitem) treeObject;
			treeitem.setOpen(isOpen);
		}
		Collection<?> com = treeObject.getChildren();
		if (com != null) {
			for (Iterator<?> iterator = com.iterator(); iterator.hasNext();) {
				collapseTree((Component) iterator.next(), isOpen);
			}
		}
	}
}
