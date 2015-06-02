/**
 * 
 */
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
public class B70_ZK_2764 extends SelectorComposer<Component> {
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
	
	//causes the problem
	@Listen("onClick = #updateButton")
	public void updateData() {
		updateDataRecursively(treeModel.getRoot());
	}

	private void updateDataRecursively(TreeNode<String> node) {
		node.setData(node.getData() + ".");
		List<TreeNode<String>> children = node.getChildren();
		for (TreeNode<String> child : children) {
			updateDataRecursively(child);
		}
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
				)
			),
			newNode("node 2",
				newNode("node 2.1")
			),
			newNode("node 3",
				newNode("node 3.1")
			),
			newNode("node 4",
				newNode("node 4.1")
			),
			newNode("node 5",
				newNode("node 5.1")
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
}