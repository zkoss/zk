package org.zkoss.zktest.test2.select.models;

import java.util.*;

import org.zkoss.zul.*;
public class ListModelLists {
	public static final int DEFAULT = 0;
	public static final int MULTIPLE = 1;
	public static final int CLONEABLE = 2;
	public static final int MULTIPLE_AND_CLONEABLE = 3;
	private static final int defaultAmount = 300;
	
	public static ListModelList getModel(int config) {
		return getModel(config,defaultAmount);
	}
	
	public static ListModelList getModel(int config,int items) {
		ListModelList model = null;
		if (config == CLONEABLE || config == MULTIPLE_AND_CLONEABLE)
			model = (ListModelList)org.zkoss.zktest.util.Serializations.toCloneableListModelList(getItems(items));
		else
			model = new ListModelList(getItems(items));
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
	public static int getNumberFromData(String data) {
		return Integer.parseInt(data.replace("data", "").trim());
	}
	private static List getItems(int items) {
		List Items = new ArrayList();
		for (int i = 0; i < items; i++) {
			Items.add(new String("data "+i));
		}
		return Items;
	}
}
