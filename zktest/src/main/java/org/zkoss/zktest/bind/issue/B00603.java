package org.zkoss.zktest.bind.issue;

import org.zkoss.zul.ListModelList;

/**
 * @author Dennis Chen
 * 
 */
public class B00603 {
	ListModelList<Item> items = new ListModelList<Item>();

	public B00603() {
		items = new ListModelList<Item>();
		items.add(new Item("A"));
		items.add(new Item("B"));
		items.add(new Item("C"));
	}

	public ListModelList<Item> getItems() {
		return items;
	}

	static public class Item {
		String name;
		
		ListModelList<Option> options = new ListModelList<Option>();

		public Item(String name) {
			this.name = name;
			options.add(new Option(name+" A",name +" AA"));
			options.add(new Option(name+" B",name +" BB"));
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ListModelList<Option> getOptions() {
			return options;
		}
	}
	
	static public class Option{
		String name;
		String value;
		public Option(String name,String value){
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
}
