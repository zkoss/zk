/* B95_ZK_4646Composer.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 27 17:16:42 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;

/**
 * @author rudyhuang
 */
public class B95_ZK_4646Composer extends SelectorComposer<Component> {
	private static final long serialVersionUID = 3814570327995355261L;

	@Wire
	private Tree tree;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		List<DefaultTreeNode<String>> children = new ArrayList<>();
		children.add(new DefaultTreeNode<>("invisible"));
		children.add(new DefaultTreeNode<>("another"));
		DefaultTreeModel<String> treeModel = new DefaultTreeModel<>(new DefaultTreeNode<>("ROOT",
				Arrays.asList(new DefaultTreeNode<>("David", children),
						new DefaultTreeNode<>("Thomas", new ArrayList<>()),
						new DefaultTreeNode<>("Steven", new ArrayList<>()))));
		tree.setItemRenderer(new PartialHideTreeRenderer());
		tree.setModel(treeModel);
	}

	private final class PartialHideTreeRenderer implements TreeitemRenderer<DefaultTreeNode<String>> {
		@Override
		public void render(final Treeitem treeItem, DefaultTreeNode<String> treeNode, int index) throws Exception {
			treeItem.setLabel(treeNode.getData());
			if (treeNode.getData().contains("invisible")) {
				treeItem.setVisible(false);
			}
		}
	}
}
