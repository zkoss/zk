/* F85_ZK_3932VM.java

        Purpose:
                
        Description:
                
        History:
                Wed Jun 27 18:05:53 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.ListModelList;

public class F85_ZK_3932VM {
	
	private ListModelList<String> listModel = new ListModelList<>();
	private DefaultTreeModel treeModel;
	
	@Init
	public void init() {
		List<DefaultTreeNode> list = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			listModel.add("row" + i);
			list.add(new DefaultTreeNode("row" + i));
		}
		treeModel = new DefaultTreeModel(new DefaultTreeNode(null, list));
	}
	
	public ListModelList<String> getListModel() {
		return listModel;
	}
	
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}
}
