package org.zkoss.zktest.test2;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModels;

public class B65_ZK_2409VM {
	private List items;
	private ListModel subModelItem;

	public B65_ZK_2409VM() {
		final NumberFormat nf = new DecimalFormat("00");
		items = new ArrayList(5);
		for (int i = 0; i < 5; i++) {
			items.add(nf.format(new Integer(i)));
		}
		subModelItem = ListModels.toListSubModel(new ListModelList(items), new Comparator() {
			public int compare(Object val, Object item) {
				String text = String.valueOf(val);
				String id = String.valueOf(item);
				return id.startsWith(text) ? 0 : -1;
			}
		}, 100);

	}

	public ListModel getSubModelItem() {
		return subModelItem;
	}
}
