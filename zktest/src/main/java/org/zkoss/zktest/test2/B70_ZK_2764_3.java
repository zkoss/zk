/** B70_ZK_2764_3.java.

	Purpose:
		
	Description:
		
	History:
		10:03:57 AM Jun 4, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

/**
 * @author jumperchen
 *
 */
public class B70_ZK_2764_3 extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	private int count = 0;
	
	@Wire("#dyntree")
	private Tree tree;

	private DefaultTreeModel<String> treeModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		treeModel = new DefaultTreeModel<String>(newNode("root", level1Nodes()));

		tree.setItemRenderer(getTreeItemRenderer());
		tree.setModel(treeModel);
	}

	@Listen("onClick = #updateButton")
	public void updateData() {
		updateDataPreorder(treeModel.getRoot().getChildren().get(0));
	}

	@Listen("onClick = #updateButtonReverse")
	public void updateDataReverse() {
		updateDataPostorder(treeModel.getRoot().getChildren().get(0));
	}

	@Listen("onClick = #updateButtonRandom")
	public void updateRandomNodes() {
		Random random = new Random();
		for(int i = 0; i < 10; i++) {
			TreeNode<String> randomNode = null;
			while(randomNode == null) {
				switch(random.nextInt(3)) {
				case 0:
					randomNode = nodeAt(random.nextInt(5));
					break;
				case 1:
					randomNode = nodeAt(random.nextInt(5), random.nextInt(2));
					break;
				case 2:
					randomNode = nodeAt(random.nextInt(5), random.nextInt(2), random.nextInt(2));
					break;
				} 
			}
			updateNode(randomNode);
		}
	}
	
	private void updateDataPreorder(TreeNode<String> node) {
		List<TreeNode<String>> children = node.getChildren();
		updateNode(node);
		for (TreeNode<String> child : children) {
			updateDataPreorder(child);
		}
	}

	private void updateDataPostorder(TreeNode<String> node) {
		List<TreeNode<String>> children = node.getChildren();
		for (TreeNode<String> child : children) {
			updateDataPostorder(child);
		}
		updateNode(node);
	}
	
	
	private void updateNode(TreeNode<String> node) {
		node.setData(node.getData() + ".");
	}
	
	//workaround
	@Listen("onClick = #rebuildButton")
	public void rebuildModel() {
		//backup open paths
		int[][] openPaths = treeModel.getOpenPaths();
		treeModel.clearOpen();
		TreeNode<String> root = treeModel.getRoot();
		root.getChildren().clear();
		root.getChildren().addAll(level1Nodes());
		//restore open paths (optional)
		treeModel.addOpenPaths(openPaths);
		
		
	};
	
	@SuppressWarnings("unchecked")
	private List<TreeNode<String>> level1Nodes() {
		return Arrays.asList(
			newNode("node 1", 
				newNode("node 1.1", 
					newNode("node 1.1.1"), 
					newNode("node 1.1.2") 
				),
				newNode("node 1.2", 
					newNode("node 1.1.1"), 
					newNode("node 1.1.2") 
				)
			),
			newNode("node 2",
				newNode("node 2.1", 
					newNode("node 2.1.1"), 
					newNode("node 2.1.2") 
				)
			),
			newNode("node 3",
				newNode("node 3.1", 
					newNode("node 3.1.1"), 
					newNode("node 3.1.2") 
				),
				newNode("node 3.2", 
					newNode("node 3.2.1"), 
					newNode("node 3.2.2") 
				)
			),
			newNode("node 4",
				newNode("node 4.1",
					newNode("node 4.1.1"), 
					newNode("node 4.1.2") 
				)
			),
			newNode("node 5",
				newNode("node 5.1",
					newNode("node 5.1.1"), 
					newNode("node 5.1.2") 
				)
			));
	};

	private TreeNode<String> newNode(String data, Collection<TreeNode<String>> children) {
		return new DefaultTreeNode<String>(treeLabel(data), children);
	}

	private TreeNode<String> newNode(String data, TreeNode<String>... children) {
		return new DefaultTreeNode<String>(treeLabel(data), children);
	}
	
	private String treeLabel(String data) {
		return data + " (" + count++ + ")";
	}
	
	private TreeitemRenderer<TreeNode<String>> getTreeItemRenderer() {
		return new TreeitemRenderer<TreeNode<String>>() {
			public void render(Treeitem item, TreeNode<String> data, int index) throws Exception {
				Treerow treerow = item.getTreerow();
				if(treerow == null) {
					treerow = new Treerow();
					item.appendChild(treerow);
					treerow.appendChild(new Treecell(data.getData()));
				} else {
					System.out.println("got here"); //never got here in my tests
					Treecell firstcell = (Treecell) treerow.getFirstChild();
					firstcell.setLabel(data.getData());
				}
			}
		};
	}
	
	private TreeNode<String> nodeAt(int... path) {
		return treeModel.getChild(path);
	}
}