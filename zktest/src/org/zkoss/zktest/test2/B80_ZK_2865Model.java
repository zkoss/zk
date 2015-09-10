/* B80_ZK_2865Model.java

	Purpose:
		
	Description:
		
	History:
		9:21 AM 9/10/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
		import java.util.Collection;

		import org.zkoss.zul.DefaultTreeModel;
		import org.zkoss.zul.DefaultTreeNode;
		import org.zkoss.zul.TreeNode;

public class B80_ZK_2865Model extends DefaultTreeModel<String> {
	private static final long serialVersionUID = 1L;

	public B80_ZK_2865Model(String data) {
		super(buildStaticModel(data));
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
		return new DefaultTreeNode<String>(data, (Collection<? extends TreeNode<String>>) Arrays
				.asList(children));
	}
}