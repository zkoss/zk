package org.zkoss.zktest.test2;

import java.util.Collections;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;

public class B96_ZK_5368VM {

	private boolean show;

	private DefaultTreeModel treeModel;

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	@Init
	public void init() {
		DefaultTreeNode<String> root = new DefaultTreeNode<>("root", Collections.emptyList());
		treeModel = new DefaultTreeModel(root);
		recursivelyFillNodes(root, "item-", 3);
		this.show = true;
	}

	private void recursivelyFillNodes(DefaultTreeNode node, String label, int i) {
		for (int j = 0; j < 5; j++) {
			DefaultTreeNode child = new DefaultTreeNode(label + j, Collections.emptyList());
			node.add(child);
			treeModel.addOpenObject(child);
			if (i > 0)
				recursivelyFillNodes(child, (String) child.getData(), i - 1);
		}
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	@NotifyChange("*")
	@Command
	public void refresh() {
	}

	@NotifyChange("show")
	@Command
	public void toggle() {
		show = !show;
	}

	@NotifyChange("show")
	@Command
	public void remove() {
		final DefaultTreeNode root = (DefaultTreeNode) treeModel.getRoot();
		root.remove(root.getChildAt(0));
	}

	@Command
	public void update(@BindingParam("target") DefaultTreeNode target) {
		target.setData(target.getData() + "-foo");
	}
}