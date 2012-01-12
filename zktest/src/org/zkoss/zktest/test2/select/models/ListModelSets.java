package org.zkoss.zktest.test2.select.models;

import java.util.*;
import org.zkoss.zul.ListModelSet;

public class ListModelSets {
	public static final int DEFAULT = 0;
	public static final int MULTIPLE = 1;
	public static final int CLONEABLE = 2;
	public static final int MULTIPLE_AND_CLONEABLE = 3;
	public static ListModelSet getModel(int config) {
		ListModelSet model = null;
		if (config == CLONEABLE || config == MULTIPLE_AND_CLONEABLE)
			model = (ListModelSet)org.zkoss.zktest.util.Serializations.toCloneableListModelSet(getItems());
		else
			model = new ListModelSet(getItems());
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
			int i1 = ListModelSets.getNumberFromData(o1.toString());
			int i2 = ListModelSets.getNumberFromData(o2.toString());
			if (_asc && i1 > i2 || !_asc && i1 < i2)
				return 1;
			return -1;
		}
	}
	public static int getNumberFromData(String data) {
		return Integer.parseInt(data.substring(data.lastIndexOf("data")+4, data.length()).trim());
	}
	private static Set getItems() {
		Set Items = new LinkedHashSet();
		for (int i = 0; i < 1000; i++) {
			Items.add(new String("data "+i));
		}
		return Items;
	}
}
