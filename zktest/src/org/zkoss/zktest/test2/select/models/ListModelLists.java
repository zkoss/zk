package org.zkoss.zktest.test2.select.models;

import java.util.*;

import org.zkoss.zul.*;
public class ListModelLists {
	public static final int DEFAULT = 0;
	public static final int MULTIPLE = 1;
	public static final int CLONEABLE = 2;
	public static final int MULTIPLE_AND_CLONEABLE = 3;
	public static ListModelList getModel(int config) {
		ListModelList model = null;
		if (config == 2 || config == 3)
			model = (ListModelList)org.zkoss.zktest.util.Serializations.toCloneableListModelList(getItems());
		else
			model = new ListModelList(getItems());
		if(config == 1 || config == 3)
			model.setMultiple(true);
		return model;
	}
	private static List getItems() {
		List Items = new ArrayList();
		new ArrayList();
		for (int i = 0; i < 1000; i++) {
			Items.add(new String("data "+i));
		}
		return Items;
	}
}
