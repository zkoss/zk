/* ListboxModelVM.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
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
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
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
	
	
	byte[] _bytes;
	public void doSerialize(Window win,Label msg){
		doSerialize0(win, msg);
		doDeserialize0(win, msg);
	}
	public void doSerialize0(Window win,Label msg){
		try{
			Page pg = win.getPage();
			win.sessionWillPassivate(pg);
			ByteArrayOutputStream oaos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(oaos);
			oos.writeObject(win);
			oos.close();
			oaos.close();
			_bytes = oaos.toByteArray();
//			msg.setValue("size : "+oaos.toByteArray().length);	
		}catch(Exception x){
			x.printStackTrace();
			msg.setValue("error :"+x.getClass()+","+x.getMessage());
		}
	}
	
	public void doDeserialize0(Window win, Label msg) {
		try{
			ByteArrayInputStream oaos = new ByteArrayInputStream(_bytes);
			ObjectInputStream oos = new ObjectInputStream(oaos);
			
			Window newwin = (Window) oos.readObject();
			Page pg = win.getPage();
			Component parent = win.getParent();
			Component ref = win.getNextSibling();
			win.detach();
			oos.close();
			oaos.close();
			parent.insertBefore(newwin, ref);
			newwin.sessionDidActivate(pg);
			Messagebox.show("done Serialize/Deserialize");
//			msg.setValue("done deserialize: "+_bytes.length);	
		}catch(Exception x){
			x.printStackTrace();
			msg.setValue("error deserialize:"+x.getClass()+","+x.getMessage());
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
