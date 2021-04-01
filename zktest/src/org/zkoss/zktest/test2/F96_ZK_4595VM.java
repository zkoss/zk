package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zktest.test2.select.models.ListModelArrays;
import org.zkoss.zktest.test2.select.models.ListModelMaps;
import org.zkoss.zktest.test2.select.models.ListModelSets;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.ListModelSet;
import org.zkoss.zul.SimpleListModel;

public class F96_ZK_4595VM {
	private ListModelList<Data> listModelList = new ListModelList<>();
	private ListModelArray listModelArray = ListModelArrays.getModel(ListModelArrays.DEFAULT, 30);
	private ListModelSet listModelSet = ListModelSets.getModel(ListModelSets.DEFAULT, 30);
	private ListModelMap listModelMap = ListModelMaps.getModel(ListModelMaps.DEFAULT, 30);
	private DefaultTreeModel dTreeModel;
	private SimpleListModel<Data> simpleListModel;

	@Init
	public void init() {
		listModelList.add(new Data("c", "cat"));
		listModelList.add(new Data("a", "ace"));
		listModelList.add(new Data("a", "ape"));
		dTreeModel = new DefaultTreeModel(
				new DefaultTreeNode(null,
						new DefaultTreeNode[]{
								new DefaultTreeNode("item 1"),
								new DefaultTreeNode("item 2",
										new DefaultTreeNode[]{
												new DefaultTreeNode("item 21"),
												new DefaultTreeNode("item 22"),
												new DefaultTreeNode("item 23"),
												new DefaultTreeNode("item 24"),
												new DefaultTreeNode("item 25"),
												new DefaultTreeNode("item 26")
										}
								),
								new DefaultTreeNode("item 3"),
								new DefaultTreeNode("item 4"),
								new DefaultTreeNode("item 5"),
								new DefaultTreeNode("item 6",
										new DefaultTreeNode[]{
												new DefaultTreeNode("item 61"),
												new DefaultTreeNode("item 62"),
												new DefaultTreeNode("item 63"),
												new DefaultTreeNode("item 64"),
												new DefaultTreeNode("item 65"),
												new DefaultTreeNode("item 66")
										}
								)
						}
				));

		List<Data> list = new ArrayList<>();
		for (int i = 0; i < 30; ++i) {
			list.add(new Data("type " + i, "name" + i));
		}
		simpleListModel = new SimpleListModel<>(list);
	}

	@Command
	public void addAndSort() {
		listModelList.add(new Data("d", "dog"));
		listModelList.add(new Data("b", "bird"));
		listModelList.sort();
		listModelArray.set(0, "data 100"); //can't add
		listModelArray.sort();
		listModelSet.add("data 1000");
		listModelSet.add("data 00");
		listModelSet.sort();
//		listModelMap.put("item 00", "data 00"); //bug, issue opened ZK-4848
		((DefaultTreeNode) dTreeModel.getRoot()).add(new DefaultTreeNode("item 2-2"));
		dTreeModel.sort();
		simpleListModel.getElementAt(0).setType("type 100"); //can't add
		simpleListModel.sort();
	}

	public ListModelList<Data> getListModelList() {
		return listModelList;
	}

	public ListModelArray getListModelArray() {
		return listModelArray;
	}

	public ListModelSet getListModelSet() {
		return listModelSet;
	}

	public ListModelMap getListModelMap() {
		return listModelMap;
	}

	public DefaultTreeModel getdTreeModel() {
		return dTreeModel;
	}

	public SimpleListModel getSimpleListModel() {
		return simpleListModel;
	}

	public class Data {
		private String type;
		private String name;

		public Data(String type, String name) {
			this.type = type;
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}


}
