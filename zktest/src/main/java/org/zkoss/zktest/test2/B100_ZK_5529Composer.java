/* B100_ZK_5529Composer.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 14 10:47:20 CST 2023, Created by rebeccalai

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Collections;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;

public class B100_ZK_5529Composer extends SelectorComposer<Component> {
	@Wire
	private Tree myTree;
	DefaultTreeModel model;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		DefaultTreeNode<String> root = new DefaultTreeNode<>("root", Collections.emptyList());
		model = new DefaultTreeModel<>(root);
		buildNodeRecursively(root, 5);
		myTree.setModel(model);
	}

	private void buildNodeRecursively(DefaultTreeNode<String> node, int j) {
		for (int i = 0; i < 100; i++) {
			j--;
			DefaultTreeNode<String> child = new DefaultTreeNode<>("node-" + j + "-" + i, Collections.emptyList());
			node.add(child);
			if (j > 0)
				buildNodeRecursively(child, j);
		}
	}
}
