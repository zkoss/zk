package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.ValidationContext;

public class B01899ComboboxReloadSelectedItem {
	private List items;
	private Item selItem;
	private Item selItem2;
	public B01899ComboboxReloadSelectedItem() {
		items = new ArrayList();
		items.add(new Item("01"));
		items.add(new Item("02"));
		items.add(new Item("03"));
		
		selItem2 = new Item("04");
	}
	
	public List getItems () {
		return items;
	}
	
	public Item getSelItem () {
		return selItem;
	}
	
	public void setSelItem (Item selItem) {
		this.selItem = selItem;
	}
	
	public Item getSelItem2 () {
		return selItem2;
	}
	
	public void setSelItem2 (Item selItem) {
		this.selItem2 = selItem;
	}
	
	public Validator getValidator(){
	    return new Validator();
	}
	
	public static class Validator extends org.zkoss.bind.validator.AbstractValidator {
		 public void validate(ValidationContext ctx) {
           if (ctx.getProperty().getValue() == null)
           	addInvalidMessage(ctx, "Please select an item!!");
       }
	}
	
	public static class Item {
		private String name;
		private Item subItem;
		
		public Item (String name) {
			this.name = name;
		}
		
		public Item (String name, Item subItem) {
			this.name = name;
			this.subItem = subItem;
		}
		public String getName () {
			return name;
		}
		
		
		public Item getSubItem () {
			return subItem;
		}
		public void setSubItem (Item subItem) {
			this.subItem = subItem;
		}
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Item other = (Item) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
		public String toString(){
			return getClass().getSimpleName()+"@"+Integer.toHexString(System.identityHashCode(this))+"["+name+"]";
		}
	}
}
