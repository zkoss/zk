package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Init;

public class ComponentSwitch {

	Item item1;
	Item item2;

	@Init
	public void init() {
		item1 = new Item("Item 1");
		item2 = new Item("Item 2");
	}

	public Item getItem1() {
		return item1;
	}

	public void setItem1(Item item1) {
		this.item1 = item1;
	}

	public Item getItem2() {
		return item2;
	}

	public void setItem2(Item item2) {
		this.item2 = item2;
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
