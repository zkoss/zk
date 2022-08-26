package org.zkoss.zktest.test2;

import java.util.Arrays;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;

public class B96_ZK_5107VM {
	private DefaultTreeModel treeModel = new DefaultTreeModel(
			new DefaultTreeNode(new B96_ZK_5107Data("root"),
				Arrays.asList(new DefaultTreeNode[]{new DefaultTreeNode(
					new B96_ZK_5107Data("David")),
					new DefaultTreeNode(new B96_ZK_5107Data("Thomas")),
					new DefaultTreeNode(new B96_ZK_5107Data("Steven"))}
				)
			)
	);

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	public B96_ZK_5107VM() {
		treeModel.setMultiple(true);
	}

	@Command
	public void treeEditRow(@BindingParam("node") B96_ZK_5107Data node) {
		node.setEdit(true);
		BindUtils.postNotifyChange(null, null, node, "edit");
	}

	@Command
	public void treeConfirmRow(@BindingParam("node") B96_ZK_5107Data node) {
		node.setEdit(false);
		BindUtils.postNotifyChange(null, null, node, "edit");
	}
}

