/* ListboxModelVM.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;

/**
 * @author Dennis Chen
 * 
 */
public class F00743_1 {
	private String message1;

	List<Item> items;
	Set<Item> selected;

	public F00743_1() {
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

	public Set<Item> getSelected() {
		return selected;
	}

	public void setSelected(Set<Item> selected) {
		this.selected = selected;
	}
	
	List sort(Set set){
		if(set==null) return null;
		ArrayList list = new ArrayList((Set)set);
		Collections.sort(list, new Comparator<Item>() {

			public int compare(Item o1, Item o2) {
				// TODO Auto-generated method stub
				return o1.getName().compareTo(o2.getName());
			}
		});
		return list;
	}
	
	public Converter getSelectedConverter(){
		return new Converter() {
			
			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				return sort((Set)val);
			}
			
			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				return val;
			}
		};
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
		public String toString(){
			return name;
		}
	}

	@Command @NotifyChange({"items","message1"}) 
	public void reload() {
		message1 = "reloaded "+(selected==null?"no selection":sort(selected));
	}
	@Command @NotifyChange({"selected","message1"}) 
	public void select() {
		message1 = "select";
		if(selected==null){
			selected = new HashSet<Item>();
		}else{
			selected.clear();
		}
		selected.add(items.get(1));
		selected.add(items.get(3));
	}
	@Command @NotifyChange({"selected","message1"}) 
	public void clean1() {
		message1 = "clean1";
		selected = null;
	}
	@Command @NotifyChange({"selected","message1"}) 
	public void clean2() {
		message1 = "clean2";
		selected.clear();
	}

}
