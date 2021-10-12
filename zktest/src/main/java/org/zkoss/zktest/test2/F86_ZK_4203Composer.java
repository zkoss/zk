package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.LinkedList;

	public class F86_ZK_4203Composer extends SelectorComposer {
		@Wire
		private Tree tree;
		@Wire
		private Label label;
		boolean initialized = false;

		@Override
		public void doAfterCompose(Component component) throws Exception {
			super.doAfterCompose(component);
			DefaultTreeNode<String> root = new DefaultTreeNode<String>("root", new LinkedList<>());
			TreeModel model = new MyDefaultTreeModel(root);
			tree.setModel(model);

			int numChild = 60;
			for (int i = 0; i < numChild; i++ ){
				root.add(new DefaultTreeNode<String>(root.getData() + "." + i));
			}
			initialized = true;
		}

		@Listen("onClick = #btn")
		public void clickToShowCount() {
			label.setValue(((MyDefaultTreeModel)tree.getModel()).getCount() + "");
		}
		class MyDefaultTreeModel extends DefaultTreeModel{
			private int count = 0;
			public int getCount() {
				return count;
			}
			public MyDefaultTreeModel(TreeNode root) {
				super(root, false);
			}

			@Override
			public TreeNode getChild(TreeNode parent, int index) {
				TreeNode node = super.getChild(parent, index);
				if (initialized)
					count++;
				return node;
			}
		}

	}