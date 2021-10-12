/* B80_ZK_2901Composer.java

	Purpose:
		
	Description:
		
	History:
		12:53 PM 10/27/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.event.TreeDataEvent;

/**
 * @author jumperchen
 */
public class B80_ZK_2901Composer  extends GenericForwardComposer {

	Tree tree;
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		tree = (Tree)comp.getFellow("tree");

		tree.setItemRenderer(new TreeitemRenderer() {
			public void render(Treeitem item, Object data, int index) {

				Treerow treerow = new Treerow();
				treerow.setParent(item);

				Treecell treecell = new Treecell();
				treecell.setParent(treerow);
				treecell.appendChild(new Label((String)((TestNode)data).getLabel()));


			}
		});


		List<TestNode> list = new ArrayList<TestNode>();

		for( int i=0; i<1500; ++i) {
			list.add(new TestNode("" +i));
		}


		TestNode node = new TestNode("main",list);
		TestTreeModel model = new TestTreeModel(node);

		tree.setModel(model);
	}


	public void onClick$btnRemove(Event e) throws InterruptedException{

		// Remove a bunch of nodes from the model and fire a TreeDataEvent.INTERVAL_REMOVED for each
		//
		((TestTreeModel)tree.getModel()).remove();

		// Invalidating the tree solves the problem, but should this really be needed?
		// Seems that Chrome is just fine without it, so why is it needed in IE11?
		//
		// tree.invalidate();
	}
	public class TestNode {

		List<TestNode> children;

		String label;

		public TestNode(String label) {
			this(label, new ArrayList<TestNode>());
		}

		public TestNode(String label, List<TestNode> children) {
			this.label = label;
			this.children = children;
		}

		public String getLabel() {
			return label;
		}
		public List<TestNode> getChildren() {
			return children;
		}
	}

	public class TestTreeModel extends AbstractTreeModel {

		public TestTreeModel(TestNode root) {
			super(root);
		}

		public void remove() {

			// I know that it would be better to fire one event here with different from and to index.
			// This is just to point out the problem
			//
			for( int i=0; i<1490; ++i) {
				((TestNode)getRoot()).getChildren().remove(0);
				this.fireEvent(getRoot(), 0, 0, TreeDataEvent.INTERVAL_REMOVED);
			}
		}

		public Object getChild(final Object parentObject, final int index)
		{
			TestNode node = (TestNode)parentObject;
			return node.getChildren().get(index);
		}

		public int getChildCount(final Object parentObject)
		{
			return ((TestNode)parentObject).getChildren().size();
		}

		public boolean isLeaf(final Object parentObject)
		{
			return getChildCount(parentObject) == 0;
		}

		public int getIndexOfChild(Object parent, Object child)
		{
			TestNode node = (TestNode)parent;
			return node.getChildren().indexOf((TestNode)child);
		}
	}

}