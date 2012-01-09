package org.zkoss.zktest.test2.select.models;

import org.zkoss.zul.ListModelArray;

public class ListModelArrays {
	public static final int DEFAULT = 0;
	public static final int MULTIPLE = 1;
	public static final int CLONEABLE = 2;
	public static final int MULTIPLE_AND_CLONEABLE = 3;

	public static ListModelArray getModel(int config) {
		ListModelArray model = null;
		if (config == CLONEABLE || config == MULTIPLE_AND_CLONEABLE)
			model = (ListModelArray)org.zkoss.zktest.util.Serializations.toCloneableListModelAraay(getItems());
		else
			model = new ListModelArray(getItems());
		if(config == MULTIPLE || config == MULTIPLE_AND_CLONEABLE)
			model.setMultiple(true);
		return model;
	}
	private static String[] getItems() {
		String[] Items = new String[1000];
		for (int i = 0; i < 1000; i++) {
			Items[i] = new String("data "+i);
		}
		return Items;
	}
}
