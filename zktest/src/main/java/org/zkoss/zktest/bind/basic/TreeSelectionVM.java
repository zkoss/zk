/* CollectionIndexComposer.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

/**
 * @author Dennis Chen
 * 
 */
public class TreeSelectionVM{
	private String message1;

	DefaultTreeModel<String> model;
	MyTreeNode root;
	MyTreeNode selected;

	public TreeSelectionVM() {
		root = new MyTreeNode("Root",
				new MyTreeNode[] {});
		String[] labs = new String[]{"A","A"};
		for (int i = 0; i < 2; i++) {
			MyTreeNode ni = new MyTreeNode(labs[i] + "-" + i,
					new MyTreeNode[] {});
			ni.setOpen(true);
			for (int j = 0; j < 3; j++) {
				MyTreeNode nj = new MyTreeNode(ni.getData()
						+ "-" + j, new MyTreeNode[] {});
				nj.setOpen(true);
				for (int k = 0; k < 2; k++) {
					MyTreeNode nk = new MyTreeNode(
							nj.getData() + "-" + k);
					nj.add(nk);
				}
				ni.add(nj);
			}
			root.add(ni);
		}
		model = new DefaultTreeModel<String>(root);
	}
	
	

	public MyTreeNode getSelected() {
		return selected;
	}



	public void setSelected(MyTreeNode selected) {
		this.selected = selected;
	}



	public TreeModel<TreeNode<String>> getModel() {
		return model;
	}

	public String getMessage1() {
		return message1;
	}

	static public class MyTreeNode extends DefaultTreeNode<String> {

		boolean open;

		public MyTreeNode(String data, MyTreeNode[] children) {
			super(data,children);
		}
		public MyTreeNode(String data) {
			super(data);
		}

		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean open) {
			this.open = open;
		}

	}
	
	@Command @NotifyChange({"items","message1","selected"}) 
	public void reload() {
		message1 = "reloaded "+ (selected==null?"no selection":selected.getData());
	}
	@Command @NotifyChange({"selected","message1"}) 
	public void select() {
		message1 = "select";
		selected = (MyTreeNode)root.getChildAt(1).getChildAt(1).getChildAt(1);
	}
	@Command @NotifyChange({"selected","message1"}) 
	public void clean() {
		message1 = "clean";
		selected = null;
	}
	
	@Command @NotifyChange("message1")
	public void showselection(){
		if(model.isSelectionEmpty()){
			message1 = "no selection";
		}else{
			int path[] = model.getSelectionPath();
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for(int i=0;i<path.length;i++){
				if(i!=0){
					sb.append(", ");
				}
				sb.append(path[i]);
			}
			sb.append("]");
			message1 = sb.toString();
		}
	}
}
