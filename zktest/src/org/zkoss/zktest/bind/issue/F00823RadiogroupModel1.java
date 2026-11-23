/* F00823RadiogroupModel.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author Dennis Chen
 * 
 */
public class F00823RadiogroupModel1 {

	List<Item> items ;
	int index = -1;
	Item selected;

	public F00823RadiogroupModel1() {
		items = new ArrayList<Item>();
		items.add(new Item("A"));
		items.add(new Item("B"));
		items.add(new Item("C"));
		items.add(new Item("D"));
		selected = null;
	}

	public List<Item> getItems() {
		return items;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}




	public Item getSelected() {
		return selected;
	}

	public void setSelected(Item selected) {
		this.selected = selected;
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
	
	@Command @NotifyChange({"index","selected"})
	public void select(){
		index = 0;
		selected = items.get(0);
	}
	
	@Command @NotifyChange({"index","selected"})
	public void clean(){
		index = -1;
		selected = null;
	}

}
