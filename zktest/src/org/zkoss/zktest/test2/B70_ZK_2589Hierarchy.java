package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

public class B70_ZK_2589Hierarchy<T> {
	private B70_ZK_2589Hierarchy<T> parent;
	private List<B70_ZK_2589Hierarchy<T>> children;
	private T data;
	private boolean leaf;

	public B70_ZK_2589Hierarchy(T childData, boolean leaf) {
		this.data = childData;
		this.leaf = leaf;
	}
	public B70_ZK_2589Hierarchy<T> getParent() {
		return parent;
	}
	public void setParent(B70_ZK_2589Hierarchy<T> parent) {
		this.parent = parent;
	}
	public List<B70_ZK_2589Hierarchy<T>> getChildren() {
		return children;
	}
	
	public T getData() {
		return data;
	}
	public void addChildData(T childData, boolean leaf) {
		if(children == null) {
			children = new ArrayList<B70_ZK_2589Hierarchy<T>>();
		}
		B70_ZK_2589Hierarchy<T> child = new B70_ZK_2589Hierarchy<T>(childData, leaf);
		child.setParent(this);
		this.getChildren().add(child);
	}
	
	public int getLevel() {
		if(this.parent == null) {
			return 0;
		} else {
			return this.getParent().getLevel() + 1;
		}
	}
	public boolean isLeaf() {
		return leaf;
	}
}
