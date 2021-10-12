package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.SimpleGroupsModel;

public class B96_ZK_4921VM {

	private SimpleGroupsModel groupsModel;

	@Init
	public void init() {
		buildModel();
	}


	public void buildModel() {
		List<List<String>> mainList = new LinkedList<>();
		mainList.add(Arrays.asList(new String[]{"group 1-option 1", "group 1-option 2", "group 1-option 3", "group 1-option 4", "group 1-option 5"}));
		mainList.add(Arrays.asList(new String[]{"group 2-option 1", "group 2-option 2"}));

		if (groupsModel == null || groupsModel.getSelection().isEmpty()) {
			groupsModel = new SimpleGroupsModel(mainList, Arrays.asList(new String[]{"group 1", "group 2"}));
			groupsModel.addToSelection(mainList.get(0).get(0));
		} else {
			mainList.add(Arrays.asList(new String[]{"group 3-option 1", "group 3-option 2"}));
			groupsModel = new SimpleGroupsModel(mainList, Arrays.asList(new String[]{"group 1", "group 2", "group 3"}));
			groupsModel.addToSelection(mainList.get(0).get(0));
		}

	}

	@Command
	@NotifyChange({"groupsModel"})
	public void refresh() {
	}

	@Command
	@NotifyChange({"groupsModel"})
	public void update() {
		buildModel();
	}

	@Command
	public void changeSelection() {
		groupsModel.addToSelection(groupsModel.getChild(1, 1));
	}


	public SimpleGroupsModel getGroupsModel() {
		return groupsModel;
	}
}
