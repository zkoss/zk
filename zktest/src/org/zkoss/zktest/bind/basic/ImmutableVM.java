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

import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Immutable;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.zk.ui.Component;

/**
 * @author Dennis Chen
 * 
 */
public class ImmutableVM {
	

	Item item;

	public ImmutableVM() {
		item = new Item("A");
	}
	public Item getItem() {
		return item;
	}
	public Option getOption() {
		return item.getOption();
	}

	public Converter getConverter1(){
		return new Converter(){

			@Override
			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				return ((Item)val).getName();
			}

			@Override
			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				return null;
			}
			
		};
	}
	
	public Converter getConverter2(){
		return new Converter(){

			@Override
			public Object coerceToUi(Object val, Component component, BindContext ctx) {
				return ((Option)val).getName();
			}

			@Override
			public Object coerceToBean(Object val, Component component, BindContext ctx) {
				return null;
			}
			
		};
	}
	
	@Command 
	public void cmd1(){
		item.setName("B");
		BindUtils.postNotifyChange(null, null, item, ".");
	}
	
	@Command 
	public void cmd2(){
		item.getOption().setName("B-option");
		BindUtils.postNotifyChange(null, null, item.option, ".");
	}


	@Immutable
	static public class Item {
		String name;
		Option option;
		public Item(String name) {
			this.name = name;
			option = new Option(name+"-option");
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Option getOption() {
			return option;
		}

		public void setOption(Option option) {
			this.option = option;
		}
		
		
	}
	static public class Option {
		String name;

		public Option(String name) {
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
