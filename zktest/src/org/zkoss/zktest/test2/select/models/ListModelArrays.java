package org.zkoss.zktest.test2.select.models;

import java.util.Comparator;

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
	public static Comparator getRowComparator(boolean asc) {
		return new MyRowComparator(asc);
	}
	private static class MyRowComparator implements Comparator, java.io.Serializable {
		boolean _asc;
		public MyRowComparator(boolean asc) {
			_asc = asc;
		}
		public int compare(Object o1, Object o2) {
			int i1 = ListModelLists.getNumberFromData((String)o1);
			int i2 = ListModelLists.getNumberFromData((String)o2);
			if (_asc && i1 > i2 || !_asc && i1 < i2)
				return 1;
			return -1;
		}
	}
	private static String[] getItems() {
		String[] Items = new String[1000];
		for (int i = 0; i < 1000; i++) {
			Items[i] = new String("data "+i);
		}
		return Items;
	}
}
