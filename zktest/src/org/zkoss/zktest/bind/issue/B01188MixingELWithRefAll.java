package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;


public class B01188MixingELWithRefAll {

	List data;
	
	TreeModel treedata;
	
	public B01188MixingELWithRefAll(){
		data = new ArrayList();
		data.add(new Item("Item 1"));
		data.add(new Item("Item 2"));
		TreeNode root = new DefaultTreeNode(new Item("Item 1"), new ArrayList());
		treedata = new DefaultTreeModel(root);
		
		TreeNode n1 = new DefaultTreeNode(new Item("Item 1"), new ArrayList()); 
		root.add(n1);
		root.add(new DefaultTreeNode(new Item("Item 2")));
		
		n1.add(new DefaultTreeNode(new Item("Item 1-1")));
		n1.add(new DefaultTreeNode(new Item("Item 1-2")));
	}

	public class Item {
		String s_;
		public Item(String s) {
			s_ = s;
		}

		public String toString(){
			return s_;
		}
	}

	public List getData() {
		return data;
	}
	
	public TreeModel getTreedata(){
		return treedata;
	}

}
