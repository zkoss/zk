/* CollectionTemplateDependenceVM.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.basic;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author Dennis Chen
 * 
 */
public class CollectionTemplateDependenceVM{


	List<Item> items ;

	public CollectionTemplateDependenceVM() {
		items = new ArrayList<Item>();
		items.add(new Item("A"));
		items.add(new Item("B"));
		items.add(new Item("C"));
		items.add(new Item("D"));
		items.add(new Item("E"));
	}

	public List<Item> getItems() {
		return items;
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
		public String toString(){
			return super.toString()+":"+name;
		}

	}

	@Command @NotifyChange("items")
	public void change1() {
		items.get(0).setName("X");
		items.get(1).setName("A");
	}
	
	@Command
	public void change2(@ContextParam(ContextType.BINDER) Binder binder) {
		items.get(0).setName("A");
		items.get(1).setName("X");
		binder.notifyChange(items.get(0),"*");
		binder.notifyChange(items.get(1),"*");
	}

}
