/* B80_ZK_3308composer.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec  2 14:44:11 CST 2016, Created by wenninghsu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;

import java.util.Comparator;

/**
 * 
 * @author wenninghsu
 */
public class B80_ZK_3308composer extends SelectorComposer<Component> {

	@Wire
	private Grid grid;

	@Wire
	private Listbox lb;

	@Wire
	private Button btn1;

	@Wire
	private Button btn2;

	GroupsModelArray myModel1 = new GroupsModelArray(new String[]{"a", "a", "b", "b", "c", "c"}, new MyComparator());

	GroupsModelArray myModel2 = new GroupsModelArray(new String[]{"a", "a", "b", "b", "c", "c"}, new MyComparator());

	class MyComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			String str1 = (String) o1;
			String str2 = (String) o2;
			return str1.compareToIgnoreCase(str2);
		}
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		grid.setModel(myModel1);
		lb.setModel(myModel2);
	}

	@Listen("onClick = #btn1")
	public void showHideGridGroup(){
		int all_group = myModel1.getGroupCount();
		for (int i = 0; i < all_group; i++) {
			boolean showHide = !myModel1.isGroupOpened(i);
			if(showHide){
				myModel1.addOpenGroup(i);
			}else{
				myModel1.removeOpenGroup(i);
			}
		}
	}

	@Listen("onClick = #btn2")
	public void showHideListboxGroup(){
		int all_group = myModel2.getGroupCount();
		for (int i = 0; i < all_group; i++) {
			boolean showHide = !myModel2.isGroupOpened(i);
			if(showHide){
				myModel2.addOpenGroup(i);
			}else{
				myModel2.removeOpenGroup(i);
			}
		}
	}

}
