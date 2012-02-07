/* CollectionIndexComposer.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

/**
 * @author Dennis Chen
 * 
 */
public class B00714 {
	private String message1;

	List<Item> items = new ArrayList<Item>();
	
	Item selected;

	public B00714() {
		items = new ListModelList<Item>();
		for(int i=0;i<1;i++){
			items.add(new Item("A"+i));
		}
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
	
	

	public Item getSelected() {
		return selected;
	}

	public void setSelected(Item selected) {
		this.selected = selected;
	}

	@NotifyChange("items") @Command
	public void reload() {
		
	}
	
	@NotifyChange({"items","selected"}) @Command
	public void delete(@BindingParam("item") Item item) {
		items.remove(item);
		if(item.equals(selected)){
			selected = null;
		}
	}

}
