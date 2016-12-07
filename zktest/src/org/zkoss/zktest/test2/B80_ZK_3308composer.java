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

import java.util.Comparator;

/**
 * 
 * @author wenninghsu
 */
public class B80_ZK_3308composer extends SelectorComposer<Component> {

	@Wire
	private Grid grid;

	@Wire
	private Button btn;

	GroupsModelArray myModel = new GroupsModelArray(new String[]{"a", "a", "b", "b", "c", "c"}, new MyComparator());

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
		grid.setModel(myModel);
	}

	@Listen("onClick = #btn")
	public void showHideGroup(){
		int all_group = myModel.getGroupCount();
		for (int i = 0; i < all_group; i++) {
			boolean showHide = !myModel.isGroupOpened(i);
			if(showHide){
				myModel.addOpenGroup(i);
			}else{
				myModel.removeOpenGroup(i);
			}
		}
	}

}
