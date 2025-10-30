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
import java.util.Arrays;
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
	private Radiogroup radiogroup;
	private ListModelList modelOfRadiogroup;
	private Listbox listbox1;
	private ListModelList modelOfListbox;
	private Grid grid1;
	private ListModelList modelOfGrid;
	private Combobox combobox;
	private ListModelList modelOfCombobox;
	private Selectbox selectbox;
	private ListModelList modelOfSelectbox;
	private Chosenbox chosenbox;
	private ListModelList modelOfChosenbox;
	private Tabbox tabbox;
	private ListModelList modelOfTabbox;
	private Listbox listbox2;
	private SimpleGroupsModel groupsModelOfListbox;
	private Grid grid2;
	private SimpleGroupsModel groupsModelOfGrid;
	private Tree tree;
	private DefaultTreeModel modelOfTree;
	private Chart chart;
	private SimpleCategoryModel modelOfChart;
	private Field listModelListeners = prepareListenersField(AbstractListModel.class);
	private Field groupsModelListeners = prepareListenersField(AbstractGroupsModel.class);
	private Field treeModelListeners = prepareListenersField(AbstractTreeModel.class);
	private Field chartModelListeners = prepareListenersField(AbstractChartModel.class);

	public B86_ZK_3977Composer() throws NoSuchFieldException {
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		initModel();
		initComponent();
	}

	private void initModel() {
		String[] strings = new String[]{"A", "B", "C"};
		modelOfRadiogroup = new ListModelList(strings);
		modelOfListbox = new ListModelList(strings);
		modelOfGrid = new ListModelList(strings);
		modelOfCombobox = new ListModelList(strings);
		modelOfSelectbox = new ListModelList(strings);
		modelOfChosenbox = new ListModelList(strings);
		modelOfTabbox = new ListModelList(strings);

		List list = new ArrayList();
		list.add(new ArrayList<>(Arrays.asList(strings)));
		list.add(new ArrayList<>(Arrays.asList(strings)));
		list.add(new ArrayList<>(Arrays.asList(strings)));
		groupsModelOfListbox = new SimpleGroupsModel(list, null, null);
		groupsModelOfGrid = new SimpleGroupsModel(list, null, null);

		DefaultTreeNode root = new DefaultTreeNode(null, new DefaultTreeNode[]{
				new DefaultTreeNode("A"), new DefaultTreeNode("B"), new DefaultTreeNode("C")});
		modelOfTree = new DefaultTreeModel(root);

		modelOfChart = new SimpleCategoryModel();
		modelOfChart.setValue("1", "A", 100);
		modelOfChart.setValue("2", "B", 200);
		modelOfChart.setValue("3", "C", 300);
	}

	private void initComponent() {
		radiogroup = new Radiogroup();
		radiogroup.setModel(modelOfRadiogroup);

		listbox1 = new Listbox();
		listbox1.setModel(modelOfListbox);

		listbox2 = new Listbox();
		listbox2.setModel(groupsModelOfListbox);

		grid1 = new Grid();
		grid1.setModel(modelOfGrid);

		grid2 = new Grid();
		grid2.setModel(groupsModelOfGrid);

		combobox = new Combobox();
		combobox.setModel(modelOfCombobox);

		selectbox = new Selectbox();
		selectbox.setModel(modelOfSelectbox);

		chosenbox = new Chosenbox();
		chosenbox.setModel(modelOfChosenbox);

		tabbox = new Tabbox();
		tabbox.setModel(modelOfTabbox);

		tree = new Tree();
		tree.setModel(modelOfTree);

		chart = new Chart();
		chart.setModel(modelOfChart);
	}

	@Listen("onClick = #toggle")
	public void toggle() {
		List<Component> children = container.getChildren();
		if (!children.isEmpty())
			children.clear();
		else {
			container.appendChild(radiogroup);
			container.appendChild(listbox1);
			container.appendChild(listbox2);
			container.appendChild(grid1);
			container.appendChild(grid2);
			container.appendChild(combobox);
			container.appendChild(selectbox);
			container.appendChild(chosenbox);
			container.appendChild(tabbox);
			container.appendChild(tree);
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

	@Listen("onClick = #modifyModel")
	public void modifyModel() {
		modelOfRadiogroup.clear();
		modelOfListbox.clear();
		modelOfGrid.clear();
		modelOfCombobox.clear();
		modelOfSelectbox.clear();
		modelOfChosenbox.clear();
		modelOfTabbox.clear();
		modelOfChart.clear();
		((DefaultTreeNode) modelOfTree.getRoot()).getChildren().clear();
		for (int i = 0; i < groupsModelOfListbox.getGroupCount(); i++) {
			groupsModelOfListbox.removeOpenGroup(i);
			groupsModelOfGrid.removeOpenGroup(i);
		}
	}

	@Listen("onClick = #showDataInfo")
	public void showDataInfo() {
		List<Boolean> open = new ArrayList<>();
		for (int i = 0; i < listbox2.getGroupCount(); i++) {
			open.add(listbox2.getGroups().get(i).isOpen());
			open.add(grid2.getRows().getGroups().get(i).isOpen());
		}
		List<Integer> size = new ArrayList<>();
		size.addAll(Arrays.asList(radiogroup.getChildren().size(),
				listbox1.getChildren().size(), grid1.getRows().getChildren().size(),
				combobox.getChildren().size(), tabbox.getTabs().getChildren().size(),
				chart.getChildren().size(), tree.getTreechildren().getItems().size()));
		Clients.log(open.toString() + size.toString());
	}

	private int getListModelListenersSize(ListModel listModel) throws IllegalAccessException {
		return ((List) listModelListeners.get(listModel)).size();
	}

	private int getGroupsModelListenersSize(GroupsModel groupsModel) throws IllegalAccessException {
		return ((List) groupsModelListeners.get(groupsModel)).size();
	}

	private Field prepareListenersField(Class cls) throws NoSuchFieldException {
		Field listeners = cls.getDeclaredField("_listeners");
		listeners.setAccessible(true);
		return listeners;
	}
}
