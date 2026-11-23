package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;


public class B70_ZK_2098_ViewModel {

	private String[] colTemplates = {"col60", "col100", "col60", "col80", "default"};
	
	public DefaultTreeModel<String> getTreeModel() {
		List<DefaultTreeNode<String>> children = new ArrayList<DefaultTreeNode<String>>();
		TreeNode<String> root = new DefaultTreeNode<String>("root", children);
		for (int i = 0; i < 10; i++) {
			root.getChildren().add(new DefaultTreeNode<String>("c" + i));
		}
		DefaultTreeModel<String> model = new DefaultTreeModel<String>(root);

		return model;
	}

	public List<ColInfo> getCols() {
		return Arrays.asList(
				new ColInfo("1", colTemplates[0], true), 
				new ColInfo("2", colTemplates[1], true), 
				new ColInfo("3", colTemplates[2], true), 
				new ColInfo("4", colTemplates[3], true), 
				new ColInfo("5", colTemplates[4], true) 
				);
	}

	public List<String> getItems() {
		List<String> result = new ArrayList<String>();
		result.add("item_1");
		result.add("item_2");
		result.add("item_3");
		result.add("item_4");
		result.add("item_5");
		return result;
	}

	@Command("changeColumnTemplates")
	public void doChangeColumnTemplates() {
		colTemplates = new String[]{"col100", "col100", "default", "col60", "col60"};
		BindUtils.postNotifyChange(null, null, this, "cols");
	}

	@Command("changeColumnTemplatesWithFlex")
	public void doChangeColumnTemplatesWithFlex() {
		colTemplates = new String[]{"col60", "col60", "col80", "flex", "flex"};
		BindUtils.postNotifyChange(null, null, this, "cols");
	}

	public String cellTemplate(int index) {
		if (index == getCols().size() - 1) {
			return "fill";
		} else {
			return "cell";
		}
	}

	public class ColInfo {
		private String label;
		private String template;
		private boolean visible;

		public ColInfo(String label, String template, boolean visible) {
			super();
			this.label = label;
			this.template = template;
			this.visible = visible;
		}
		
		public String getLabel() {
			return label;
		}
		
		public String getTemplate() {
			return template;
		}

		public boolean isVisible() {
			return visible;
		}
	}
}
