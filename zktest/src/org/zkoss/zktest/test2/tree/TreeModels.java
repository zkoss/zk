/* TreeModels.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jan 6, 2012 12:37:38 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.tree;

import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

/**
 *
 * @author simonpai
 */
public class TreeModels {
	
	public static DefaultTreeModel<String> createDefaultTreeModel(boolean closeEnd) {
		return new DefaultTreeModel<String>(createNode(RAW_NODES, closeEnd));
	}
	
	public static DefaultTreeModel<String> createCloneableDefaultTreeModel(boolean closeEnd) {
		return new CloneableTreeModel<String>(createNode(RAW_NODES, closeEnd));
	}
	
	private static class CloneableTreeModel<String> extends DefaultTreeModel<String> implements ComponentCloneListener, Cloneable {
		public CloneableTreeModel(TreeNode<String> root) {
			super(root);
		}

		@Override
		public Object willClone(Component comp) {
			return clone();
		}
	}
	/**
	 * Open all nodes.
	 */
	public static <T> void openAll(DefaultTreeModel<T> model, boolean open) {
		openAllChildren(model, model.getRoot(), open);
	}
	
	private static <T> void openAllChildren(DefaultTreeModel<T> model, 
			TreeNode<T> node, boolean open) {
		model.setOpen(node, open);
		for (TreeNode<T> c : node.getChildren())
			openAllChildren(model, c, open);
	}
	
	
	
	/**
	 * Return TreeModel selection.
	 */
	public static String printSelection(DefaultTreeModel<String> model) {
		Set<?> objs = model.getSelection();
		return objs == null ? "(null)" : objs.isEmpty() ? "(empty)" : "[" + join(objs) + "]";
	}
	
	@SuppressWarnings("unchecked")
	private static TreeNode<String> createNode(Object[] objs, boolean closeEnd) {
		String name = (String) objs[0];
		int len = objs.length - 1;
		if (closeEnd && len == 0)
			return new TestDefaultTreeNode(name);
		TreeNode<String>[] children = new TreeNode[len];
		for (int i = 0; i < len; i++)
			children[i] = createNode((Object[]) objs[i + 1], closeEnd);
		return new TestDefaultTreeNode(name, children);
	}
	
	public static class TestDefaultTreeNode extends DefaultTreeNode<String> {
		
		private static final long serialVersionUID = 4650156098416941063L;
		
		public TestDefaultTreeNode(String data) {
			super(data);
		}
		
		public TestDefaultTreeNode(String data, TreeNode<String>[] children) {
			super(data, children);
		}
		
		@Override
		public String toString() {
			String data = getData();
			return data == null ? "(null)" : data.toString();
		}
		
	}
	
	private final static Object[] RAW_NODES = new Object[] {
		"Root",
		new Object[] {
				"A",
				new Object[] {
						"B",
						new Object[] { "C" },
						new Object[] { "D" },
						new Object[] { "E" }
				},
				new Object[] { "F" }
		},
		new Object[] {
				"G",
				new Object[] { "H" },
				new Object[] {
						"I",
						new Object[] { "J" }
				},
				new Object[] { "K" },
				new Object[] {
						"L",
						new Object[] { "M" },
						new Object[] { "N" },
						new Object[] { "O" }
				}
		},
		new Object[] {
				"P",
				new Object[] { "Q" }
		},
		new Object[] {
				"R",
				new Object[] {
						"S",
						new Object[] {
								"T",
								new Object[] { "U" },
								new Object[] { "V" },
								new Object[] { "W" },
								new Object[] { "X" },
								new Object[] { "Y" }
						}
				},
				new Object[] { "Z" }
		}
	};
	
	public static String join(Iterable<?> objs) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object obj : objs) {
			if (!first)
				sb.append(", ");
			else
				first = false;
			sb.append(obj);
		}
		return sb.toString();
	}
	
}
