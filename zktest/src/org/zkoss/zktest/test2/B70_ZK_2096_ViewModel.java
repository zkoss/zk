package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

public class B70_ZK_2096_ViewModel {

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}

	public DefaultTreeModel<String> getTreeModel() {
		List<DefaultTreeNode<String>> children = new ArrayList<DefaultTreeNode<String>>();
		TreeNode<String> root = new DefaultTreeNode<String>("root", children);
		for (int i = 0; i < 10; i++) {
			root.getChildren().add(new DefaultTreeNode<String>("c" + i));
		}
		DefaultTreeModel<String> model = new DefaultTreeModel<String>(root);

		return model;
	}

	public List<String> getCols() {
		List<String> result = new ArrayList<String>();
		result.add("col_1");
		result.add("col_2");
		result.add("col_3");
		return result;
	}

	public List<String> getItems() {
		List<String> result = new ArrayList<String>();
		result.add("item_1");
		result.add("item_2");
		result.add("item_3");
		return result;
	}

	@Command
	public void doSomething() {
		BindUtils.postNotifyChange(null, null, this, "cols");
	}

	public boolean isVisible(String name) {
		return true;
		// return name.indexOf("_2") == -1;
	}
}
