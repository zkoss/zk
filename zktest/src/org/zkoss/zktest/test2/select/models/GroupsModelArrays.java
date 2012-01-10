package org.zkoss.zktest.test2.select.models;

import org.zkoss.zul.ArrayComparator;
import org.zkoss.zul.GroupsModelArray;

public class GroupsModelArrays {
	public static GroupsModelArray getModel() {
		GroupsModelArray model = null;
		model = new GroupsModelArray(getItems(), new ArrayComparator(0, true));
		return model;
	}
	private static Object[][] getItems () {
		Object[][] items = new Object[2000][3];
		for (int i = 0; i < 2000; i++) {
			Object[] item = {new String("Itemset "+i/10), new String("Value "+i), new String("Description "+i)};
			items[i] = item;
		}
		return items;
	}
}