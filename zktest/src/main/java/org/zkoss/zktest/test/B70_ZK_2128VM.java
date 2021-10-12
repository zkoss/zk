package org.zkoss.zktest.test;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

public class B70_ZK_2128VM {
	private boolean showAux = true;

	  @AfterCompose
	  public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
	    Selectors.wireComponents(view, this, false);
	  }

	  public DefaultTreeModel<String> getTreeModel() {
	    List<DefaultTreeNode<String>> children = new ArrayList<DefaultTreeNode<String>>();
	    TreeNode<String> root = new DefaultTreeNode<String>("root", children);
	    for (int i = 0; i < 7; i++) {
	      root.getChildren().add(new DefaultTreeNode<String>("c" + i));
	    }
	    DefaultTreeModel<String> model = new DefaultTreeModel<String>(root);

	    return model;
	  }

	  public List<String> getCols() {
	    List<String> result = new ArrayList<String>();
	    result.add("Fixed column");
	    result.add("col_1 col_1 col_1");
	    result.add("col_2");
	    result.add("col_3");
	    result.add("");
	    return result;
	  }

	  public List<String> getItems() {
	    List<String> result = new ArrayList<String>();
	    result.add("fixed item");
	    result.add("1");
	    result.add("2");
	    result.add("3");
	    result.add("4");
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

	  @Command
	  public void toggleAux(@BindingParam("state") Boolean state) {
	    this.showAux = state;
	    BindUtils.postNotifyChange(null, null, this, "cols");
	    BindUtils.postNotifyChange(null, null, this, "showAux");
	  }

	  public void setShowAux(boolean showAux) {
	    System.out.println("showAux " + this.showAux);
	    this.showAux = showAux;
	  }

	  public boolean isShowAux() {
	    System.out.println("showAux " + this.showAux);
	    return this.showAux;
	  }

	  public String colTemplate(int index) {
	    if (index == 0) {
	      return "col0";
	    } else if (index == getCols().size() - 1) {
	      return "fill";
	    } else {
	      return "col";
	    }
	  }

	  public String auxTemplate(int index) {
	    if (index == 0) {
	      return "aux0";
	    } else if (index == getCols().size() - 1) {
	      return "fill";
	    } else {
	      return "aux";
	    }
	  }

	  public String cellTemplate(int index) {
	    if (index == 0) {
	      return "cell0";
	    } else if (index == getCols().size() - 1) {
	      return "fill";
	    } else {
	      return "cell";
	    }
	  }
}
