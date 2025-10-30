/* BindingParamVM.java

		Purpose:
		
		Description:
		
		History:
				Mon May 03 14:47:30 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class BindingParamVM {
	private String message1;

	List<BindingParamVM.Item> items;

	public BindingParamVM() {
		items = new ArrayList<Item>();
		items.add(new Item("A"));
		items.add(new Item("B"));
		items.add(new Item("C"));
		items.add(new Item("D"));
	}

	public List<Item> getItems() {
		return items;
	}

	@NotifyChange({ "message1" }) @Command
	public void showIndex(@BindingParam("index") Integer index) {
		message1 = "item index " + index;
	}

	@NotifyChange({ "items", "message1" }) @Command
	public void update(@BindingParam("item") Item item, @BindingParam("addName") String name) {
		String newName = item.getName() + name;
		item.setName(newName);
		message1 = "updated item name to: " + newName;
	}

	public String getMessage1() {
		return message1;
	}

	static public class Item {
		String name;

		public Item(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
