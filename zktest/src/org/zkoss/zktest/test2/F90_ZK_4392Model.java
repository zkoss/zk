/* F90_ZK_4392Model.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 8 10:26:11 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.Collection;

import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

public class F90_ZK_4392Model extends DefaultTreeModel<String> {
	private static final long serialVersionUID = 1L;

	public F90_ZK_4392Model(String data) {
		super(buildStaticModel(data));
		this.addToSelection(this.getChild(new int[]{1,1}));
	}

	public static TreeNode<String> buildStaticModel(String data) {
		DefaultTreeNode<String> root = node("",
				node(data + "-1",
						node(data + "-1-1",
								node(data + "-1-1-1"),
								node(data + "-1-1-2"),
								node(data + "-1-1-3")
						),
						node(data + "-1-2",
								node(data + "-1-2-1"),
								node(data + "-1-2-2"),
								node(data + "-1-2-3")
						),
						node(data + "-1-3",
								node(data + "-1-3-1"),
								node(data + "-1-3-2"),
								node(data + "-1-3-3")
						)
				),
				node(data + "-2",
						node(data + "-2-1"),
						node(data + "-2-2"),
						node(data + "-2-3")
				),
				node(data + "-3",
						node(data + "-3-1"),
						node(data + "-3-2"),
						node(data + "-3-3")
				)
		);
		return root;
	}

	@SuppressWarnings("unchecked")
	public static DefaultTreeNode<String> node(String data, TreeNode<?>... children) {
		return new DefaultTreeNode<String>(data, Arrays.asList((TreeNode<String>[])children));
	}
}