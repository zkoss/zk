package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.SimpleGroupsModel;

import java.util.ArrayList;
import java.util.List;

public class B70_ZK_2812_VM {
	private SimpleGroupsModel groupmodel;

	public SimpleGroupsModel getGroupmodel() {
		return groupmodel;
	}

	@Init
	public void init(){
		List head = new ArrayList();
		List data = new ArrayList();
		head.add("group1");
		head.add("group2");
		List subData1 = new ArrayList();
		List subData2 = new ArrayList();
		subData1.add("1");
		subData1.add("2");
		subData2.add("3");
		subData2.add("4");
		data.add(subData1);
		data.add(subData2);
		groupmodel = new SimpleGroupsModel(data, head);
		for (int i = 0, len = groupmodel.getGroupCount(); i < len; i++) {
			groupmodel.removeOpenGroup(i);
		}
	}

	@Command
	public void open(){
		Clients.showNotification("Open Group");
	}
}
