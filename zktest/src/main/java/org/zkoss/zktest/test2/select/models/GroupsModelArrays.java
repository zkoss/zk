package org.zkoss.zktest.test2.select.models;

import org.zkoss.zul.ArrayComparator;
import org.zkoss.zul.GroupsModelArray;

public class GroupsModelArrays {
	private static final int itemCnt = 600;
	public static GroupsModelArray getModel() {
		GroupsModelArray model = null;
		model = new GroupsModelArray(getItems(), new ArrayComparator(0, true));
		return model;
	}
	private static Object[][] getItems () {
		Object[][] items = new Object[itemCnt][3];
		int setCnt = -1;
		String setStr = "";
		for (int i = 0; i < itemCnt; i++) {
			// make the groups order ascending
			if (setCnt != i/3) {
				setCnt = i/3;
				setStr = setCnt + "";
				while (setStr.length() < 3)
					setStr = "0"+setStr;
			}
			Object[] item = {new String("Itemset "+setStr), new String("Value "+i), new String("Description "+i)};
			items[i] = item;
		}
		return items;
	}
}