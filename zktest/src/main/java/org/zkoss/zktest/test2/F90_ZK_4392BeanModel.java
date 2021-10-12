/* F90_ZK_4392BeanModel.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 8 10:26:11 CST 2019, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.Collection;

import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

public class F90_ZK_4392BeanModel extends DefaultTreeModel<F90_ZK_4392BeanModel.User> {
	private static final long serialVersionUID = 1L;

	public F90_ZK_4392BeanModel(String data) {
		super(buildStaticModel(data));
	}

	public static TreeNode<User> buildStaticModel(String data) {
		DefaultTreeNode<User> root = node("",
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
	public static DefaultTreeNode<User> node(String data, TreeNode<?>... children) {
		return new DefaultTreeNode<User>(new User(data), (Collection<? extends TreeNode<User>>) Arrays
				.asList((TreeNode<User>[])children));
	}

	public static class User {
		private String name;

		public User(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}