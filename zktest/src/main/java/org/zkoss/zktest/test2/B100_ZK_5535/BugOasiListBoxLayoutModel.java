package org.zkoss.zktest.test2.B100_ZK_5535;

import java.util.LinkedList;

import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;
public class BugOasiListBoxLayoutModel {
	private BugOasiTreeNode<BugFormModel> treeRoot = null;
	private TreeModel<TreeNode<BugFormModel>> treeNodes = null;
	private boolean enableMultiSelection = false;
	public boolean isEnableMultiSelection() {
		return enableMultiSelection;
	}

	public void setEnableMultiSelection(boolean enableMultiSelection) {
		this.enableMultiSelection = enableMultiSelection;
	}

	public BugOasiTreeNode<BugFormModel> getTreeRoot() {
		return treeRoot;
	}

	public void setTreeRoot(BugOasiTreeNode<BugFormModel> treeRoot) {
		this.treeRoot = treeRoot;
	}
	public TreeModel<TreeNode<BugFormModel>> getTreeNodes() {
		return this.treeNodes;
	}

	public void setTreeNodes(TreeModel<TreeNode<BugFormModel>> treeNodes) {
		this.treeNodes = treeNodes;
	}
	/**
	 * <LI>OasiListBoxLayoutModel</LI>
	 * 
	 * <PRE>
	 * Aggiunge un nodo al tree
	 * </PRE>
	 * 
	 * Aggiungo nodo di root
	 * 
	 * @author m.spuri
	 */
	public BugOasiTreeNode<BugFormModel> addTreeNode(boolean open) {
		return this.addTreeNode(null, open);
	}/**
	 * <LI>OasiListBoxLayoutModel</LI>
	 * 
	 * <PRE>
	 * Aggiunge un nodo al tree
	 * </PRE>
	 * 
	 * @author m.spuri
	 */
	public BugOasiTreeNode<BugFormModel> addTreeNode(
			BugOasiTreeNode<BugFormModel> parent,
			boolean open) {
		return this.addTreeNode(parent, open, null);
	}
	public BugOasiTreeNode<BugFormModel> addTreeNode(
			BugOasiTreeNode<BugFormModel> parent,
			boolean open, Integer pos) {
		return this.addTreeNode(parent, null, null, open, pos);
	}
	private BugOasiTreeNode<BugFormModel> addTreeNode(
			BugOasiTreeNode<BugFormModel> parent,
			BugOasiTreeNode<BugFormModel> newnod, BugFormModel myrow, boolean open,
			Integer pos) {
		if (parent == null) {
			if (this.treeRoot == null)
				this.createTreeRoot();
			parent = this.treeRoot;
		}
		BugFormModel row;
		if (newnod == null) {
			if (myrow == null)
				row = new BugFormModel();
			else
				row = myrow;
		} else
			row = newnod.getData();
		
		
		BugOasiTreeNode<BugFormModel> treeNode = null;
		if (newnod == null)
			treeNode = new BugOasiTreeNode<BugFormModel>(parent, row,
					new LinkedList<BugOasiTreeNode<BugFormModel>>(), treeNodes, open,
					pos);
		else {
			treeNode = newnod;
			parent.setLeaf(false);
			if (pos == null)
				parent.add(treeNode);
			else
				parent.getChildren().add(pos, treeNode);
		}
		row.setNode(treeNode);
		if (treeNode.isOpen())
			((DefaultTreeModel<BugFormModel>) treeNodes).addOpenObject(treeNode);
		return treeNode;
	}
	private void createTreeRoot() {
		this.treeRoot = new BugOasiTreeNode<BugFormModel>(null,
				new LinkedList<BugOasiTreeNode<BugFormModel>>(), treeNodes, true);
		
		this.treeNodes = new DefaultTreeModel<BugFormModel>(this.treeRoot);
		((DefaultTreeModel<BugFormModel>)this.treeNodes).setMultiple(this.isEnableMultiSelection());
	}
	/**
	 * <LI>OasiListBoxLayoutModel</LI>
	 * 
	 * <PRE>
	 * Aggiunge un nodo al tree
	 * </PRE>
	 * 
	 * Di default � chiuso
	 * 
	 * @author m.spuri
	 */
	public BugOasiTreeNode<BugFormModel> addTreeNode(
			BugOasiTreeNode<BugFormModel> parent) {
		return this.addTreeNode(parent, false);
	}
	
	public void clearTree() {
		int n;
		if (this.treeRoot != null) {
			n = this.treeRoot.getChildren().size();
			for (int k = 0; k < n; k++)
				this.clearTreeNode((BugOasiTreeNode<BugFormModel>) this.treeRoot.getChildren().get(k));
			try {
				this.treeRoot.getChildren().clear();
			} catch (Exception e) {
				// TODO: handle exception
			}

			this.treeRoot = null;
		}
	}
	private  void clearTreeNode(BugOasiTreeNode<BugFormModel> node) {
		if (node == null)
			return;
		if (!node.isLeaf() && node.isOpen())
			((DefaultTreeModel<BugFormModel>) treeNodes).removeOpenObject(node);
		
		BugFormModel row = (BugFormModel) node.getData();
		row.detach();
		if (node.getChildren() != null) {
			int n = node.getChildren().size();
			for (int i = 0; i < n; i++) {
				this.clearTreeNode((BugOasiTreeNode<BugFormModel>) node.getChildren().get(i));
			}
			node.getChildren().clear();
		}
	}
	public void setLeafNodes() {
		int n;
		if (this.treeRoot != null) {
			n = this.treeRoot.getChildren().size();
			for (int k = 0; k < n; k++)
				this.setLeaf((BugOasiTreeNode<BugFormModel>) this.treeRoot.getChildren().get(k));
		}
	}
	private  void setLeaf(BugOasiTreeNode<BugFormModel> node) {
		if (node == null)
			return;
		if (node.getChildren() != null && node.getChildren().size()>0 ) {
			int n = node.getChildren().size();
			for (int i = 0; i < n; i++) {
				this.setLeaf((BugOasiTreeNode<BugFormModel>) node.getChildren().get(i));
			}
		}
		else
			node.setLeaf(true);
	}

}
