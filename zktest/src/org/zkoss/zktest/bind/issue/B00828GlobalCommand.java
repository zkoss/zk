/* CollectionIndexComposer.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.SelectorParam;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;

/**
 * @author Dennis Chen
 * 
 */
public class B00828GlobalCommand {

	List<Item> items ;
	
	int count;

	Label msg;
	
	@AfterCompose
	public void init(@SelectorParam("#msg") Label msg){
		this.msg = msg;
	}
	
	public B00828GlobalCommand() {
		items = new ListModelList<Item>();
		items.add(new Item("A"));
	}

	public List<Item> getItems() {
		return items;
	}

	
	static public class Item {
		String name;

		public Item(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
	
	public Converter getConverter1(){
		return new Converter(){
			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				msg.setValue(""+count++);
				return val;
			}

			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				return val;
			}
		};
	}
	
	@Command @NotifyChange("items")
	public void post(){
		//
	}

}
