/* ListboxModelVM.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.basic;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author Dennis Chen
 * 
 */
public class SelectboxModelSelectionVM {
	private String message1;

	List<Item> items ;
	
	Integer selected;
	
	public SelectboxModelSelectionVM() {
		items = new ArrayList<Item>();
		items.add(new Item("A"));
		items.add(new Item("B"));
		items.add(new Item("C"));
		items.add(new Item("D"));
		selected = 1;
	}

	public Integer getSelected() {
		return selected;
	}

	@NotifyChange
	public void setSelected(Integer selected) {
		this.selected = selected;
		System.out.println(">>>>>"+selected);
	}

	public List<Item> getItems() {
		return items;
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
	
	@Command @NotifyChange({"items","message1"}) 
	public void reload() {
		message1 = "reloaded";
	}
	
	@Command @NotifyChange({"items","message1","selected"}) 
	public void select() {
		selected = items.size()-1;
		message1 = "selected";
	}
	
	public String cat(String m1,String m2){
		return m1+":"+m2;
	}

}
