/* B86_ZK_4017Composer.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 06 11:14:22 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

/**
 * @author rudyhuang
 */
public class B86_ZK_4017ListboxComposer extends GenericForwardComposer<Component> {
	private static final long serialVersionUID = 3120643218344891700L;

	public static int counter = 0;

	private Listbox listbox;
	private ListModelList<B86_ZK_4017MyItem> model;
	private List<B86_ZK_4017MyItem> itemList;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		counter = 0;

		model = new ListModelList<>();
		listbox.setModel(model);
		//in real case, load data asynchronously, so it has to set model to Grid first
		model.addAll(getData());
		// sample natural sort, sort according to business need
		model.sort(Comparator.comparingInt(o -> o.v3), true);
	}

	private List<B86_ZK_4017MyItem> getData() {
		ArrayList<B86_ZK_4017MyItem> list = new ArrayList<>();
		itemList = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			list.add(new B86_ZK_4017MyItem("a" + (1000 - i), B86_ZK_4017Composer.repeat("b", i % 5) + i, i));
		}

		this.itemList = new ArrayList<>(list);
		return list;
	}

	private Set<B86_ZK_4017MyItem> getFilteredList() {
		final Set<B86_ZK_4017MyItem> itemsToShowAfterFilter = new HashSet<>(itemList);
		itemsToShowAfterFilter.removeIf(item -> (item.v3 % 8 == 0));
		return itemsToShowAfterFilter;
	}

	public void onClick$filterEverything(Event e) {
		changeToFilterByClearAndAdd(new HashSet<>());
	}

	public void onClick$filterByClear(Event e) {
		changeToFilterByClearAndAdd(getFilteredList());
	}

	public void onClick$clearByClear(Event e) {
		changeToFilterByClearAndAdd(new HashSet<>(itemList));
	}

	private void changeToFilterByClearAndAdd(Collection<B86_ZK_4017MyItem> filteredList) {
		counter = 0;
		if (filteredList.equals(new HashSet<>(model.getInnerList()))) {
			return;
		}
		model.clear();

		if (!filteredList.isEmpty()) {
			model.addAll(filteredList);
		}
	}
}

