/* ListboxModelVM.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * @author Dennis Chen
 * 
 */
public class B00869Serialization implements Serializable{
	private static final long serialVersionUID = 1L;

	private String message1;

	List<Item> items;
	Item selected;

	public B00869Serialization() {
		items = new ArrayList<Item>();
		items.add(new Item("A"));
		items.add(new Item("B"));
		items.add(new Item("C"));
		items.add(new Item("D"));
	}

	public List<Item> getItems() {
		return items;
	}

	public Item getSelected() {
		return selected;
	}

	public void setSelected(Item selected) {
		this.selected = selected;
	}

	public String getMessage1() {
		return message1;
	}

	static public class Item implements Serializable{
		private static final long serialVersionUID = 1L;

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
	}

	@Command
	public void save() {
	}
	
	
	
	
	public void doSerialize(Window win,Label msg){
		try{
			ByteArrayOutputStream oaos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(oaos);
			oos.writeObject(win);
			oos.close();
			oaos.close();
			msg.setValue("size : "+oaos.toByteArray().length);	
		}catch(Exception x){
			x.printStackTrace();
			msg.setValue("error :"+x.getClass()+","+x.getMessage());
		}
	}
	
	public Validator getDummyValidator(){
		return new Validator(){
			@Override
			public void validate(ValidationContext ctx) {				
			}
		};
	}
	
	public Converter getDummyConverter(){
		return new Converter() {
			
			@Override
			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				return val;
			}
			
			@Override
			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				return val;
			}
		};
	}

}
