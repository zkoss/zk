/* B86_ZK_4020Composer.java

        Purpose:
                
        Description:
                
        History:
                Thu Aug 09 10:22:34 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Strings;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;


public class B86_ZK_4020Composer extends GenericForwardComposer<Component>  {

	private static final long serialVersionUID = 3120643218344891700L;

	public static int rowCounter = 0;

	public static int itemCounter = 0;

	private Grid grid;

	private Listbox listbox;

	private ListModelList<MyItem> gridModel;

	private ListModelList<MyItem> listboxModel;

	private List<MyItem> gridItemList;

	private List<MyItem> listboxItemList;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		rowCounter = 0;
		gridModel = new ListModelList<>();
		grid.setModel(gridModel);
		// loading asynchronously
		gridModel.addAll(getGridData());
		// sample natural sort, sort according to business reed
		gridModel.sort(Comparator.comparingInt(o -> o.v3), true);
		itemCounter = 0;
		listboxModel = new ListModelList<>();
		listbox.setModel(listboxModel);
		listboxModel.addAll(getListboxData());
		listboxModel.sort(Comparator.comparingInt(o -> o.v3), true);
	}

	private List<MyItem> getGridData() {
		ArrayList<MyItem> list = new ArrayList<>();
		gridItemList = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			list.add(new MyItem("a" + (10000 - i), Strings.repeat("b", i % 5) + i, i));
		}
		
		this.gridItemList = new ArrayList<>(list);
		return list;
	}

	private List<MyItem> getListboxData() {
	ArrayList<MyItem> list = new ArrayList<>();
	listboxItemList = new ArrayList<>();
	for (int i = 0; i < 10000; i++) {
		list.add(new MyItem("a" + (10000 - i), Strings.repeat("b", i % 5) + i, i));
	}

	this.listboxItemList = new ArrayList<>(list);
	return list;
	}

	private Set<MyItem> getFilteredList() {
		final Set<MyItem> itemsToShowAfterFilter = new HashSet<>(gridItemList);
		itemsToShowAfterFilter.removeIf(item -> (item.v3 % 8 == 0));
		return itemsToShowAfterFilter;
	}
	
	public void onClick$rowFilter(Event e) {
		gridChangeToFilterByRemoveAddPartial(getFilteredList());
	}
	
	public void onClick$itemFilter(Event e) {
		listboxChangeToFilterByRemoveAddPartial(getFilteredList());
	}
	
	// original changeToFilterByRemoveAddPartial-function (with additional counter reset)
	private void gridChangeToFilterByRemoveAddPartial(Collection<MyItem> filteredList) {
		rowCounter = 0;
		final List<MyItem> toRemove = new LinkedList<>();
		
		final Set<MyItem> innerList = new HashSet<>(gridModel.getInnerList());
		for (final MyItem e : gridItemList) {
			if (!filteredList.contains(e) && innerList.contains(e)) {
			    toRemove.add(e);
			}
		}
		filteredList.removeAll(innerList);
		
		gridModel.removeAll(new HashSet<>(toRemove));
		
		if (!filteredList.isEmpty()) {
			gridModel.addAll(filteredList);
		}
	}
	
	private void listboxChangeToFilterByRemoveAddPartial(Collection<MyItem> filteredList) {
		itemCounter = 0;
		final List<MyItem> toRemove = new LinkedList<>();
		
		final Set<MyItem> innerList = new HashSet<>(listboxModel.getInnerList());
		for (final MyItem e : listboxItemList) {
			if (!filteredList.contains(e) && innerList.contains(e)) {
				toRemove.add(e);
			}
		}
		filteredList.removeAll(innerList);
		
		listboxModel.removeAll(new HashSet<>(toRemove));
		
		if (!filteredList.isEmpty()) {
			listboxModel.addAll(filteredList);
		}
	}
	public class MyItem {

		public String v1;
		public String v2;
		public int v3;
		
		private MyItem(String v1, String v2, int v3) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}
	}

}
