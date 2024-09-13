/* B101_ZK_5594_Composer.java

	Purpose:

	Description:

	History:
		2:41 PM 2024/9/13, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * @author jumperchen
 */
public class B101_ZK_5594_Composer extends GenericForwardComposer {
	private Listbox listbox;
	private Listheader lhGroup;

	private List<Item> items = new ArrayList<>();

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		for (int i = 1; i < 50; i++) {
			addGroup("Group " + i, 10);
		}
		listbox.setModel(new ListModelList<>(items));
		lhGroup.setSortAscending(Comparator.comparing(x -> ((Item)x).getGroup()));
		lhGroup.setSortDescending(lhGroup.getSortAscending().reversed());
		lhGroup.addEventListener(Events.ON_GROUP, (event) -> {
			GroupsModelArray<Item, Object, Object, Object> model = new GroupsModelArray(items.toArray(), lhGroup.getSortAscending(), 0);
			listbox.setModel(model);
		});
		listbox.setItemRenderer(new Renderer());
	}

	private void addGroup(String groupName, int numItems) {
		for (int i = 1; i <= numItems; i++) {
			Item item = new Item(groupName, String.valueOf(i));
			items.add(item);
		}
	}
	public static class Item {
		private final String group;
		private final String text;

		public Item(String group, String text) {
			this.group = group;
			this.text = text;
		}

		public String getGroup() {
			return group;
		}

		public String getText() {
			return text;
		}
	}
	public static class Renderer implements ListitemRenderer<Item> {
		public void render(Listitem item, Item data, int i) {
			item.appendChild(new Listcell(data.getGroup()));
			if (!(item instanceof Listgroup)) {
				item.appendChild(new Listcell(data.getText()));
			}
		}
	}
}

