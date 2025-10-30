/* B80_ZK_3230VM.java

	Purpose:
		
	Description:
		
	History:
		Fri, Jun 24, 2016 12:09:13 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

/**
 * 
 * @author Sefi
 */
 
import java.util.ArrayList;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
public class B80_ZK_3230VM {
	private DefaultTreeModel<DefaultTreeNode<String>> dataList;

	@Init
	public void init() {

		DefaultTreeNode rootNode = new DefaultTreeNode(null,
				new ArrayList<DefaultTreeNode<String>>(), true);
		for (int i = 0; i < 100; i++) {
			rootNode.add(new DefaultTreeNode("Item " + i));
		}
		dataList = new DefaultTreeModel(rootNode);
	}

	public DefaultTreeModel getDataList() {
		return dataList;
	}
}
