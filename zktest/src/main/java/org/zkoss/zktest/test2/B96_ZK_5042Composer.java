/* B96_ZK_5042Composer.java

	Purpose:
		
	Description:
		
	History:
		6:31 PM 2021/11/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;

/**
 * @author jumperchen
 */
public class B96_ZK_5042Composer extends SelectorComposer {
	@Wire("tree")
	private Tree tree;
	private DefaultTreeModel<DefaultTreeNode> model;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		List<DefaultTreeNode> childnodes = new LinkedList<DefaultTreeNode>();
		for (int i = 0; i < 100; ++i) {
			DefaultTreeNode node = new DefaultTreeNode("" + i, createChildNodes(i+"-", 10));
			childnodes.add(node);
		}

		DefaultTreeNode root = new DefaultTreeNode("", childnodes);

		model = new DefaultTreeModel(root);
		tree.setModel(model);

	}

	List<DefaultTreeNode> createChildNodes(String data, int count){
		List<DefaultTreeNode> childNodes = new ArrayList<DefaultTreeNode>(count);
		for (int i = 0; i < count; ++i) {
			DefaultTreeNode node = new DefaultTreeNode(data + i);
			childNodes.add(node);
		}
		return childNodes;
	}


	@Listen("onClick = #expand")
	public void expandTree() {
		model.setOpenObjects(model.getRoot().getChildren());
	}

	@Listen("onClick = #collapse")
	public void collapseTree() {
		model.setOpenObjects(Collections.emptyList());
	}
}
