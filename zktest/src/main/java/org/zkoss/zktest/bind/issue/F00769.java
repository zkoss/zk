/* CollectionIndexComposer.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zktest.bind.issue.F00743_1.Item;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

/**
 * @author Dennis Chen
 * 
 */
public class F00769{
	private String message1;

	DefaultTreeModel<String> model;
	MyTreeNode root;
	Set<MyTreeNode> selected;

	public F00769() {
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
		model.setMultiple(true);
	}
	
	

	public Set<MyTreeNode> getSelected() {
		return selected;
	}



	public void setSelected(Set<MyTreeNode> selected) {
		this.selected = selected;
	}
	
	
	public Converter getSelectedConverter(){
		return new Converter() {
			
			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				return sort((Set)val);
			}
			
			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				return val;
			}
		};
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
		
		public String toString(){
			return getData();
		}

	}
	
	List sort(Set set){
		if(set==null) return null;
		ArrayList list = new ArrayList((Set)set);
		Collections.sort(list);
		return list;
	}
	
	int[][] sort(int[][] path){
		ArrayList al = new ArrayList();
		for(int i=0;i<path.length;i++){
			al.add(path[i]);
		}
		Collections.sort(al,new Comparator() {
			public int compare(Object o1, Object o2) {
				int[] a1 = (int[])o1;
				int[] a2 = (int[])o2;
				
				for(int i=0;i<a1.length && i<a2.length;i++){
					if(a1[i]==a2[i]) continue;
					
					return new Integer(a1[i]).compareTo(new Integer(a2[i]));
				}
				return a1.length>a2.length?1:-1;
			}
		});
		
		for(int i=0;i<path.length;i++){
			path[i] = (int[])al.get(i);
		}
		
		return path;
	}
	
	@Command @NotifyChange({"items","message1","selected"}) 
	public void reload() {
		ArrayList list = null;
		if(selected!=null){
			list = new ArrayList((Set)selected);
			Collections.sort(list);
		}
		message1 = "reloaded "+ (selected==null?"no selection":list);
	}
	@Command @NotifyChange({"selected","message1"}) 
	public void select() {
		message1 = "select";
		if(selected==null){
			selected = new HashSet<MyTreeNode>();
		}else{
			selected.clear();
		}
		selected.add((MyTreeNode)root.getChildAt(0).getChildAt(1));
		selected.add((MyTreeNode)root.getChildAt(1).getChildAt(1).getChildAt(1));
	}
	@Command @NotifyChange({"selected","message1"}) 
	public void clean1() {
		message1 = "clean";
		selected = null;
	}
	@Command @NotifyChange({"selected","message1"}) 
	public void clean2() {
		message1 = "clean";
		if(selected!=null){
			selected.clear();
		}
	}
	
	@Command @NotifyChange("message1")
	public void showselection(){
		if(model.isSelectionEmpty()){
			message1 = "no selection";
		}else{
			int path[][] = sort(model.getSelectionPaths());
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for(int i=0;i<path.length;i++){
				if(i!=0){
					sb.append(", ");
				}
				sb.append("[");
				for(int j=0;j<path[i].length;j++){
					if(j!=0){
						sb.append(", ");
					}
					sb.append(path[i][j]);
				}
				sb.append("]");
			}
			sb.append("]");
			message1 = sb.toString();
		}
	}
}
