/* B96_ZK_5208Composer.java

	Purpose:
		
	Description:
		
	History:
		2:26 PM 2022/10/7, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;

import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;

/**
 * @author jumperchen
 */
public class B96_ZK_5208Composer extends SelectorComposer<Component> {
	@Wire
	private Tree tree;
	private MyTreeModel myModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);


		DefaultTreeNode<String> root = new DefaultTreeNode<String>("root", new ArrayList<DefaultTreeNode<String>>());
		final String property = Library.getProperty(
				"org.zkoss.zul.AbstractTreeModel.DefaultSelectionControl.class");
		try {
			Library.setProperty(
					"org.zkoss.zul.AbstractTreeModel.DefaultSelectionControl.class", null); // remove EE optimization
			myModel = new MyTreeModel(root);
		} finally {
			Library.setProperty(
					"org.zkoss.zul.AbstractTreeModel.DefaultSelectionControl.class", property);
		}
		buildChildrenRecursivelly(root, 5);
		myModel.setMultiple(true);
		tree.setModel(myModel);
	}

	private void buildChildrenRecursivelly(DefaultTreeNode<String> node, int depth) {
		if(depth>0) {
			for (int j = 0; j < 2; j++) {
				DefaultTreeNode<String> newNode = new DefaultTreeNode<String>(depth + " - " + j, new ArrayList<DefaultTreeNode<String>>());
				node.add(newNode);
				myModel.addOpenObject(newNode);
				buildChildrenRecursivelly(newNode, depth-1);
			}
		}
	}
	static class MyTreeModel extends AbstractTreeModel<DefaultTreeNode<String>> {

		public MyTreeModel(DefaultTreeNode<String> root) {
			super(root);
		}

		@Override
		public boolean isLeaf(DefaultTreeNode<String> node) {
			return node.getChildCount()==0;
		}

		@Override
		public DefaultTreeNode<String> getChild(DefaultTreeNode<String> parent, int index) {
			if(index < parent.getChildCount()) {
				return (DefaultTreeNode<String>) parent.getChildAt(index);
			}else {
				throw new RuntimeException("Tree tried to access item index " + index + " out of a max index of " + (parent.getChildCount() - 1));
			}
		}

		@Override
		public int getChildCount(DefaultTreeNode<String> parent) {
			return parent.getChildCount();
		}
	}
}
