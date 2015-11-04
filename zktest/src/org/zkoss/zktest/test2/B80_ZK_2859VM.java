/* B80_ZK_2859VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 27 16:40:40 CST 2015, Created by wenning

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;

/**
 * 
 * @author wenning
 */
public class B80_ZK_2859VM {

	private DefaultTreeModel<B80_ZK_2859> pagTreeModel;

	@Init
	public void init() {
		pagTreeModel = initTreeModel("pagination");
	}

	private DefaultTreeModel<B80_ZK_2859> initTreeModel(String key) {
		DefaultTreeNode<B80_ZK_2859> rootNode = new DefaultTreeNode<B80_ZK_2859>(null,
				new ArrayList<DefaultTreeNode<B80_ZK_2859>>());
		B80_ZK_2859 firstNodedata = new B80_ZK_2859(key + "-1", key + "-Node1");
		DefaultTreeNode<B80_ZK_2859> firstNode = new DefaultTreeNode<B80_ZK_2859>(firstNodedata,
				new ArrayList<DefaultTreeNode<B80_ZK_2859>>());
		generateListNode(firstNode, key);
		rootNode.add(firstNode);
		return new DefaultTreeModel<B80_ZK_2859>(rootNode);
	}

	private void generateListNode(DefaultTreeNode<B80_ZK_2859> parentNode, String key) {
		for (int i = 1; i < 11; i++) {
			B80_ZK_2859 nodeData = new B80_ZK_2859(key + "-sub".concat(Integer.toString(i)), key + "-SubNode".concat(Integer.toString(i)));
			DefaultTreeNode<B80_ZK_2859> node = new DefaultTreeNode<B80_ZK_2859>(nodeData);
			parentNode.add(node);
		}
	}

	public DefaultTreeModel<B80_ZK_2859> getPagTreeModel() {
		return pagTreeModel;
	}

}
