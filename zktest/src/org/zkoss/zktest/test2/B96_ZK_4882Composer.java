/* B96_ZK_4882Composer.java

		Purpose:
		
		Description:
		
		History:
				Thu May 13 16:09:17 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindComposer;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

public class B96_ZK_4882Composer extends BindComposer {
	private String message1;

	ListModelList<Item> items;

	public B96_ZK_4882Composer() {
		items = new ListModelList<Item>();
		items.add(new Item("A"));
		items.add(new Item("B"));
		items.add(new Item("C"));
		items.add(new Item("D"));
		items.addToSelection(items.get(1));
	}

	public String cat(String s1,String s2){
		return s1+s2;
	}
	public String cat(String s1,String s2,String s3){
		return s1+s2+s3;
	}

	public ListModelList<Item> getItems() {
		return items;
	}

	@NotifyChange({ "message1" }) @Command
	public void showIndex(@BindingParam("index") Integer index, @BindingParam("tabbox") Tabbox tabbox) {
		int i = index.intValue();
		message1 = "item index " + i;
	}

	@NotifyChange({ "items", "message1" }) @Command
	public void delete(@BindingParam("item") Item item) {
		int i = items.indexOf(item);
		items.remove(item);
		message1 = "remove item index " + i;
	}

	@NotifyChange({ "items", "message1" }) @Command
	public void addAfter(@BindingParam("item") Item item) {
		int i = items.indexOf(item);
		Item newi = new Item(item.getName() + i);
		items.add(i + 1, newi);
		message1 = "addAfter item index " + (i + 1);
	}

	@NotifyChange({ "items","message1" }) @Command
	public void addBefore(@BindingParam("item") Item item) {
		int i = items.indexOf(item);
		Item newi = new Item(item.getName() + i);
		items.add(i, newi);
		message1 = "addBefore item index " + (i + 1);
	}

	public String getMessage1() {
		return message1;
	}

	static public class Item {
		String name;

		ListModelList<String> options = new ListModelList<String>();

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

		public ListModelList<String> getOptions() {
			return options;
		}
	}
}

