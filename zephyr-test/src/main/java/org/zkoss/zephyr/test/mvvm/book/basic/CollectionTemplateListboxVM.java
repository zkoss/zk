/* CollectionIndexComposer.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zephyr.test.mvvm.book.basic;

import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis Chen
 */
public class CollectionTemplateListboxVM {
	private String message1 = "";
	List<Item> _items;

	public CollectionTemplateListboxVM() {
		_items = new ArrayList<Item>();
		for (int i = 0; i < 100; i++) {
			_items.add(new Item("A" + i));
		}
	}

	public List<Item> getItems() {
		return _items;
	}

	@NotifyChange({"message1"})
	@Command
	public void showIndex(@BindingParam("index") Integer index) {
		int i = index.intValue();
		message1 = "item index " + i;
	}

	@NotifyChange({"items", "message1"})
	@Command
	public void delete(@BindingParam("item") Item item) {
		int i = _items.indexOf(item);
		_items.remove(item);
		message1 = "remove item index " + i;
	}

	@NotifyChange({"items", "message1"})
	@Command
	public void addAfter(@BindingParam("item") Item item) {
		int i = _items.indexOf(item);
		Item newi = new Item(item.getName() + i);
		_items.add(i + 1, newi);
		message1 = "addAfter item index " + (i + 1);
	}

	@NotifyChange({"items", "message1"})
	@Command
	public void addBefore(@BindingParam("item") Item item) {
		int i = _items.indexOf(item);
		Item newi = new Item(item.getName() + i);
		_items.add(i, newi);
		message1 = "addBefore item index " + (i + 1);
	}

	public String getMessage1() {
		return message1;
	}

	static public class Item {
		String name;

		List<String> options = new ArrayList<String>();

		public Item(String name) {
			this.name = name;
			options.add(name + " 0");
			options.add(name + " 1");
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

		public String toString() {
			return super.toString() + ":" + name;
		}

	}

	@Command
	@NotifyChange("items")
	public void reload() {
		System.out.println(">>>>>>>>>>>>>>>>>reload");
	}

	@Command
	@NotifyChange("items")
	public void change1() {
		_items.get(0).setName("X");
		_items.get(1).setName("A");
	}

	@Command
	public void change2(@ContextParam(ContextType.BINDER) Binder binder) {
		_items.get(0).setName("A");
		binder.notifyChange(_items.get(0), "name");
		_items.get(1).setName("B");
		binder.notifyChange(_items.get(1), "*");
	}

	@Command
	public void delete0() {
		_items.remove(0);
	}

	@Command
	public void change0() {
		_items.set(0, new Item("CHANGED!"));
	}

	@Command
	public void add0() {
		_items.add(0, new Item("NEW!"));
	}

	public String concat(String s1, String s2) {
		return s1.concat(s2);
	}
}
