package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.zkoss.bind.ValidationContext;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModels;

public class B01891ComboboxValidator {
	private ListModel model1;
	private ListModel model2;
	private Item selItem;
	private Integer selIndex = -1;
	
	public B01891ComboboxValidator () {
		List list = new ArrayList();
		list.add(new Item("01"));
		list.add(new Item("02"));
		list.add(new Item("03"));
		model1 = ListModels.toListSubModel(
			new ListModelList(list), new ItemComparator(), 10);
		
		model2 = ListModels.toListSubModel(
				new ListModelList(list), new ItemComparator(), 10);
	}
	
	public ListModel getModel1 () {
		return model1;
	}
	public ListModel getModel2 () {
		return model2;
	}
	
	public Item getSelItem () {
		return selItem;
	}
	
	public void setSelItem (Item selItem) {
		this.selItem = selItem;
	}
	
	
	
	public Integer getSelIndex() {
		return selIndex;
	}

	public void setSelIndex(Integer selIndex) {
		this.selIndex = selIndex;
	}

	public Validator1 getValidator1(){
	    return new Validator1();
	}
	
	public Validator2 getValidator2(){
	    return new Validator2();
	}
	
	public static class Item {
		private String name;
		public Item (String name) {
			this.name = name;
		}
		public String getName () {
			return name;
		}
		public String toString(){
			return getClass().getSimpleName()+"@"+Integer.toHexString(System.identityHashCode(this))+"["+name+"]";
		}
	}
	
	public static class ItemComparator implements Comparator {
		public int compare(Object text, Object item) {
		    String val = text.toString().toLowerCase();

		    if (val.isEmpty())
		    	return 0;
		    String name = "";
		    if (item instanceof Item) {
		    	name = ((Item)item).getName();
		    }
		    return name.toLowerCase().startsWith(val)? 0: -1;
		}
	}
	
	public static class Validator1 extends org.zkoss.bind.validator.AbstractValidator {
		 public void validate(ValidationContext ctx) {
           if (ctx.getProperty().getValue() == null)
           	addInvalidMessage(ctx, "Please select an item!!");
       }
	}
	public static class Validator2 extends org.zkoss.bind.validator.AbstractValidator {
		 public void validate(ValidationContext ctx) {
          if (ctx.getProperty().getValue() == null || ((Integer)ctx.getProperty().getValue()).intValue() == -1)
          	addInvalidMessage(ctx, "Please select an item!!");
      }
	}
}
