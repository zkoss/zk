/* B80_ZK_3208VM.java

	Purpose:
		
	Description:
		
	History:
		Mon, May 16, 2016 11:24:14 AM, Created by Christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.TreeModel;

/**
 * 
 * @author Christopher
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class B80_ZK_3208VM {
	private ListModelList myModel;
	private TreeModel tmodel;
	private int myPageSize;
	
	@Init
	public void init(){
		myPageSize = 5;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 100; i++) {
			list.add("item " + i);
		}
		myModel = new ListModelList(list);
		
		List<DefaultTreeNode> node = new ArrayList<DefaultTreeNode>();
		for (int i = 0; i < 100; i++) {
			node.add(new DefaultTreeNode("item " + i));
		}
		tmodel = new DefaultTreeModel(new DefaultTreeNode(null, node));
	}
	
	public ListModelList getMyModel() {
		return myModel;
	}
	public TreeModel getTmodel() {
		return tmodel;
	}
	public int getMyPageSize() {
		return myPageSize;
	}
}
