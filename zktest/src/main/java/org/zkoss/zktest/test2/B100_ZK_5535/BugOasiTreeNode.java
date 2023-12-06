package org.zkoss.zktest.test2.B100_ZK_5535;

import java.util.LinkedList;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

public class BugOasiTreeNode<T> extends DefaultTreeNode<T> {
	private static final long serialVersionUID = -8085873079938209759L;
	
	// Node Control the default open
	private boolean open = false;
	private boolean leafNode=true;
	private TreeModel<TreeNode<T>> treeNodes=null;
	public BugOasiTreeNode(T data, LinkedList<BugOasiTreeNode<T>> children, TreeModel<TreeNode<T>>treeNodes,boolean open) {
		super(data, children);
		this.treeNodes=treeNodes;
		if ( this.treeNodes!=null )
			this.setOpen(open);
		else
		    this.open=open;
	}
	public BugOasiTreeNode(BugOasiTreeNode<T> parent,T data, LinkedList<BugOasiTreeNode<T>> children, TreeModel<TreeNode<T>>treeNodes,boolean open) {
		super(data, children);
		this.treeNodes=treeNodes;
		this.setOpen(open);
		parent.setLeaf(false);
		parent.add(this);
	}
	public BugOasiTreeNode(BugOasiTreeNode<T> parent,T data, LinkedList<BugOasiTreeNode<T>> children,TreeModel<TreeNode<T>>treeNodes, boolean open,Integer pos) {
		super(data, children);
		this.treeNodes=treeNodes;
		this.setOpen(open);
		parent.setLeaf(false);
		if (pos==null || parent.getChildren().size()==0 )
		    parent.add(this);
		else
			parent.getChildren().add(pos,this);
	}
	public BugOasiTreeNode(T data, LinkedList<BugOasiTreeNode<T>> children,TreeModel<TreeNode<T>>treeNodes) {
		super(data, children);
		this.treeNodes=treeNodes;
	}
	public BugOasiTreeNode(BugOasiTreeNode<T> parent,T data, LinkedList<BugOasiTreeNode<T>> children,TreeModel<TreeNode<T>>treeNodes) {
		super(data, children);
		parent.setLeaf(false);
		parent.add(this);
		this.treeNodes=treeNodes;
	}

	public BugOasiTreeNode(BugOasiTreeNode<T> parent,T data,TreeModel<TreeNode<T>>treeNodes) {
		super(data);
		parent.setLeaf(false);
		parent.add(this);
		this.treeNodes=treeNodes;
	}
	public BugOasiTreeNode(BugOasiTreeNode<T> parent,T data,boolean open,TreeModel<TreeNode<T>>treeNodes) {
		super(data);
		parent.setLeaf(false);
		parent.add(this);
		this.treeNodes=treeNodes;
		this.setOpen(open);
	}
	public BugOasiTreeNode(T data,TreeModel<TreeNode<T>>treeNodes) {
		super(data);
		this.treeNodes=treeNodes;
	}
	public BugOasiTreeNode(T data,TreeModel<TreeNode<T>>treeNodes,boolean open) {
		super(data);
		this.treeNodes=treeNodes;
		this.setOpen(open);
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
		if ( open )
	        ((DefaultTreeModel<T>)treeNodes).addOpenObject(this);
		else
			((DefaultTreeModel<T>)treeNodes).removeOpenObject(this);
	}
	public boolean isLeaf() {
		
			 return leafNode;
	}
	public void setLeaf(boolean value) {
		
		 this.leafNode=value;
		 if ( value )
			 this.setOpen(false);
    }
}
