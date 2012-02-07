package org.zkoss.zktest.bind.comp;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;



public class Tree{

	
	DefaultTreeModel<String> treeModel = new DefaultTreeModel<String>(
			  new DefaultTreeNode<String>("Root",
			    new DefaultTreeNode[] {
			      new DefaultTreeNode<String>("Root.0", 
			    		  new DefaultTreeNode[] {
					      new DefaultTreeNode<String>("Root.0.0"),
					      new DefaultTreeNode<String>("Root.0.1")
					    }),
			      new DefaultTreeNode<String>("Root.1",
	    		   		  new DefaultTreeNode[] {
					      new DefaultTreeNode<String>("Root.1.0"),
					      new DefaultTreeNode<String>("Root.1.1")
					    })
			    }
			  ));
	
	
	private TreeNode<String> selected= new DefaultTreeNode<String>("init");
	private boolean open =false;

	
	public TreeNode<String> getSelected() {
		return selected;
	}

	public void setSelected(TreeNode<String> selected) {
		this.selected = selected;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public TreeModel getModel(){
		return treeModel;
	}
	// -----------command -----------------
	@Command @NotifyChange("*")
	public void open(){
		open = true;
	}

}
