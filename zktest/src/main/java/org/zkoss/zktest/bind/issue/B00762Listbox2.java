/* ListboxModelVM.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zktest.bind.issue.B00762Combobox2.Item;
import org.zkoss.zul.ListModelList;

/**
 * @author Dennis Chen
 * 
 */
public class B00762Listbox2 {
	private String message1;

	ListModelList<Item> items;

	public B00762Listbox2() {
		items = new ListModelList<Item>();
		items.add(new Item("A"));
		items.add(new Item("B"));
		items.add(new Item("C"));
		items.add(new Item("D"));
	}

	public List<Item> getItems() {
		return items;
	}

	public String getMessage1() {
		return message1;
	}

	static public class Item {
		String name;
		
		List<String> options = new ArrayList<String>();

		public Item(String name) {
			this.name = name;
			options.add(name+" 0");
			options.add(name+" 1");
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getOptions() {
			return options;
		}
	}

	@Command @NotifyChange({"items","message1"}) 
	public void reload() {
		Set<Item> sels = items.getSelection();
		Item selected = sels==null || sels.size()==0?null:sels.iterator().next();
		message1 = "reloaded "+ (selected==null?"no selection":selected.name);
	}
	@Command @NotifyChange({"message1"}) 
	public void select() {
		message1 = "select";
		items.addToSelection(items.get(1));
	}
	@Command @NotifyChange({"message1"}) 
	public void clean() {
		message1 = "clean";
		items.clearSelection();
	}

}
