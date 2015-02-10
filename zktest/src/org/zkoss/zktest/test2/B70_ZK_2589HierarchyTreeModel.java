package org.zkoss.zktest.test2;

import org.zkoss.zul.AbstractTreeModel;

public class B70_ZK_2589HierarchyTreeModel extends AbstractTreeModel<B70_ZK_2589Hierarchy<String>> {

	private static final long serialVersionUID = 1L;

	public B70_ZK_2589HierarchyTreeModel(String data) {
		super(new B70_ZK_2589Hierarchy<String>(data, false));
	}

	public boolean isLeaf(B70_ZK_2589Hierarchy<String> node) {
		return node.isLeaf();
	}

	public B70_ZK_2589Hierarchy<String> getChild(B70_ZK_2589Hierarchy<String> parent, int index) {
		if(parent.getChildren() == null) {
			System.out.println("loading children of: " + parent.getData());
			//alternatively load from DB here
			for(int i = 0; i < getChildCount(parent); i++) {
				String childData = parent.getData() + "-" + (i + 1);
				parent.addChildData(childData, parent.getLevel() >= 2);
			}
		}
		return parent.getChildren().get(index);
	}

	public int getChildCount(B70_ZK_2589Hierarchy<String> parent) {
		//alternatively load from DB here
		return parent.isLeaf() ? 0 : 60;
	}

}