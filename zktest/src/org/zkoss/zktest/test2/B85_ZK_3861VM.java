/* B85_ZK_3861VM.java

	Purpose:

	Description:

	History:
			Mon Mar 12 14:09:06 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.Locale;

public class B85_ZK_3861VM {
	private ListModelList listModel = new ListModelList(Locale.getAvailableLocales());
	private DefaultTreeModel treeModel;

	@Init
	public void init() {
		listModel.setPageSize(15);
		refreshTreeModel();
		treeModel.setPageSize(15);
	}

	private void refreshTreeModel() {
		DefaultTreeNode root = new DefaultTreeNode(null, new ArrayList<>());
		for (Locale locale : Locale.getAvailableLocales()) {
			DefaultTreeNode node = new DefaultTreeNode(locale);
			root.add(node);
		}
		treeModel = new DefaultTreeModel(root);
	}

	@Command
	@NotifyChange("listModel")
	public void nextList() {
		int activePage = listModel.getActivePage();
		listModel.setActivePage(activePage + 1);
	}

	@Command
	@NotifyChange("listModel")
	public void previousList() {
		int activePage = listModel.getActivePage();
		if (--activePage < 0)
			activePage = 0;
		listModel.setActivePage(activePage);
	}

	@Command
	@NotifyChange("treeModel")
	public void nextTree() {
		int activePage = treeModel.getActivePage();
		treeModel.setActivePage(activePage + 1);
	}

	@Command
	@NotifyChange("treeModel")
	public void previousTree() {
		int activePage = treeModel.getActivePage();
		if (--activePage < 0)
			activePage = 0;
		treeModel.setActivePage(activePage);
	}

	@Command
	@NotifyChange("listModel")
	public void changeListModel() {
		listModel = new ListModelList(Locale.getAvailableLocales());
	}

	@Command
	@NotifyChange("treeModel")
	public void changeTreeModel() {
		refreshTreeModel();
	}

	public ListModelList getListModel() {
		return listModel;
	}

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}
}
