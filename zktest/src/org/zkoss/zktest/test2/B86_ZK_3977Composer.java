/* B86_ZK_3977Composer.java

        Purpose:
                
        Description:
                
        History:
                Fri Aug 10 15:44:13 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Chosenbox;
import org.zkoss.zul.AbstractChartModel;
import org.zkoss.zul.AbstractGroupsModel;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.SimpleGroupsModel;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tree;

@SuppressWarnings("unchecked")
public class B86_ZK_3977Composer extends SelectorComposer {

	@Wire
	private Div container;
	private ListModelList modelOfRadiogroup = new ListModelList();
	private ListModelList modelOfListbox = new ListModelList();
	private ListModelList modelOfGrid = new ListModelList();
	private ListModelList modelOfCombobox = new ListModelList();
	private ListModelList modelOfSelectbox = new ListModelList();
	private ListModelList modelOfChosenbox = new ListModelList();
	private ListModelList modelOfTabbox = new ListModelList();
	private SimpleGroupsModel groupsModelOfListbox = new SimpleGroupsModel(new ArrayList());
	private SimpleGroupsModel groupsModelOfGrid = new SimpleGroupsModel(new ArrayList());
	private DefaultTreeModel modelOfTree = new DefaultTreeModel(new DefaultTreeNode(null));
	private SimpleCategoryModel modelOfChart = new SimpleCategoryModel();
	private Field listModelListeners = prepareListenersField(AbstractListModel.class);
	private Field groupsModelListeners = prepareListenersField(AbstractGroupsModel.class);
	private Field treeModelListeners = prepareListenersField(AbstractTreeModel.class);
	private Field chartModelListeners = prepareListenersField(AbstractChartModel.class);

	public B86_ZK_3977Composer() throws NoSuchFieldException {
	}

	private Field prepareListenersField(Class cls) throws NoSuchFieldException {
		Field listeners = cls.getDeclaredField("_listeners");
		listeners.setAccessible(true);
		return listeners;
	}

	@Listen("onClick = #toggle")
	public void toggle() {
		List<Component> children = container.getChildren();
		if (!children.isEmpty())
			children.clear();
		else {
			Radiogroup radiogroup = new Radiogroup();
			radiogroup.setModel(modelOfRadiogroup);
			container.appendChild(radiogroup);

			Listbox listbox1 = new Listbox();
			listbox1.setModel(modelOfListbox);
			container.appendChild(listbox1);

			Listbox listbox2 = new Listbox();
			listbox2.setModel(groupsModelOfListbox);
			container.appendChild(listbox2);

			Grid grid1 = new Grid();
			grid1.setModel(modelOfGrid);
			container.appendChild(grid1);

			Grid grid2 = new Grid();
			grid2.setModel(groupsModelOfGrid);
			container.appendChild(grid2);

			Combobox combobox = new Combobox();
			combobox.setModel(modelOfCombobox);
			container.appendChild(combobox);

			Selectbox selectbox = new Selectbox();
			selectbox.setModel(modelOfSelectbox);
			container.appendChild(selectbox);

			Chosenbox chosenbox = new Chosenbox();
			chosenbox.setModel(modelOfChosenbox);
			container.appendChild(chosenbox);

			Tabbox tabbox = new Tabbox();
			tabbox.setModel(modelOfTabbox);
			container.appendChild(tabbox);

			Tree tree = new Tree();
			tree.setModel(modelOfTree);
			container.appendChild(tree);

			Chart chart = new Chart();
			chart.setModel(modelOfChart);
			container.appendChild(chart);
		}
	}

	@Listen("onClick = #showListenersSize")
	public void showListenersSize() throws IllegalAccessException {
		List<String> listOfListenersSize = new ArrayList<>();
		listOfListenersSize.add("Radiogroup: " + getListModelListenersSize(modelOfRadiogroup));
		listOfListenersSize.add("Listbox(ListModel): " + getListModelListenersSize(modelOfListbox));
		listOfListenersSize.add("Grid(ListModel): " + getListModelListenersSize(modelOfGrid));
		listOfListenersSize.add("Combobox: " + getListModelListenersSize(modelOfCombobox));
		listOfListenersSize.add("Selectbox: " + getListModelListenersSize(modelOfSelectbox));
		listOfListenersSize.add("Chosenbox: " + getListModelListenersSize(modelOfChosenbox));
		listOfListenersSize.add("Tabbox: " + getListModelListenersSize(modelOfTabbox));
		listOfListenersSize.add("Listbox(GroupsModel): " + getGroupsModelListenersSize(groupsModelOfListbox));
		listOfListenersSize.add("Grid(GroupsModel): " + getGroupsModelListenersSize(groupsModelOfGrid));
		listOfListenersSize.add("Tree: " + ((List) treeModelListeners.get(modelOfTree)).size());
		listOfListenersSize.add("Chart: " + ((List) chartModelListeners.get(modelOfChart)).size());
		Clients.log(listOfListenersSize.toString());
	}

	private int getListModelListenersSize(ListModel listModel) throws IllegalAccessException {
		return ((List) listModelListeners.get(listModel)).size();
	}

	private int getGroupsModelListenersSize(GroupsModel groupsModel) throws IllegalAccessException {
		return ((List) groupsModelListeners.get(groupsModel)).size();
	}
}
