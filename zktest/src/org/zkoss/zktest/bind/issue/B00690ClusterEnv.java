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
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * @author Dennis Chen
 * 
 */
public class B00690ClusterEnv implements Serializable{
	private static final long serialVersionUID = 1L;

	String message;
	
	List<Item> items;
	Item selected;

	public B00690ClusterEnv() {
		items = new ArrayList<Item>();
		items.add(selected = new Item("A"));
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

	

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	@Command @GlobalCommand @NotifyChange("message")
	public void save(@BindingParam("msg") String msg) {
		message = msg;
	}
	
	public Validator getDummyValidator(){
		return new AbstractValidator() {
			public void validate(ValidationContext ctx) {	
				String val = (String)ctx.getProperties(ctx.getProperty().getBase()).get("name").getValue();
				if(val!=null && val.length()>=3){
					addInvalidMessage(ctx, "that value length must samll than 2 , but is "+val.length());
				}
			}
		};
	}
	
	public Converter getDummyConverter(){
		return new Converter() {

			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				return val;
			}

			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				return val;
			}
		};
	}

}
