package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeModel;

public class B102_ZK_5923VM {
	private int pageIndex = 0;
	private int pageSize = 3;
	private int totalSize = 500;
	List<String> data;
	private TreeModel treeModel;

	public B102_ZK_5923VM() {
		data = new ArrayList<>();
		for (int i = 0; i < totalSize; i++) {
			data.add("Item-" + i);
		}
		List<DefaultTreeNode> node = new ArrayList<DefaultTreeNode>();
		for (int i = 0; i < totalSize; i++) {
			node.add(new DefaultTreeNode("item " + i));
		}
		treeModel = new DefaultTreeModel(new DefaultTreeNode(null, node));
	}

	public List<String> getData() {
		return data;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public TreeModel getTreeModel() {
		return treeModel;
	}
}