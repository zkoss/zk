package org.zkoss.zktest.test2.select.models;

import java.util.*;

import org.zkoss.zul.*;
public class ListModelMaps {
	public static final int DEFAULT = 0;
	public static final int MULTIPLE = 1;
	public static final int CLONEABLE = 2;
	public static final int MULTIPLE_AND_CLONEABLE = 3;
	private static final int defaultAmount = 300;
	
	public static ListModelMap getModel(int config) {
		return getModel(config,defaultAmount);
	}
	
	public static ListModelMap getModel(int config,int amount) {
		ListModelMap model = null;
		if (config == CLONEABLE || config == MULTIPLE_AND_CLONEABLE)
			model = (ListModelMap)org.zkoss.zktest.util.Serializations.toCloneableListModelMap(getItems(amount));
		else
			model = new ListModelMap(getItems(amount));
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
			int i1 = ListModelMaps.getNumberFromData(o1.toString());
			int i2 = ListModelMaps.getNumberFromData(o2.toString());
			if (_asc && i1 > i2 || !_asc && i1 < i2)
				return 1;
			return -1;
		}
	}
	public static int getNumberFromData(String data) {
		return Integer.parseInt(data.substring(data.lastIndexOf("data")+4, data.length()).trim());
	}
	private static Map getItems(int items) {
		Map Items = new LinkedHashMap();
		for (int i = 0; i < items; i++) {
			Items.put(new String("item " +i), new String("data "+i));
		}
		return Items;
	}
}
