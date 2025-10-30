package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;


public class B01873SelectedItemOnClick {

	private List<Item> items;
	
	private Item selectedItem;
	private SubItem clickedSubItem;

	public B01873SelectedItemOnClick() {
		items = new ArrayList<Item>();
		items.add(new Item("Item 1"));
		items.add(new Item("Item 2"));
		items.add(new Item("Item 3"));
	}

	public List<Item> getItems() {
		return items;
	}
	
	

	public SubItem getClickedSubItem() {
		return clickedSubItem;
	}

	public void setClickedSubItem(SubItem clickedSubItem) {
		this.clickedSubItem = clickedSubItem;
	}

	public Item getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Item selectedItem) {
		this.selectedItem = selectedItem;
	}

	@Command @NotifyChange("clickedSubItem")
	public void onClickSubItem(@BindingParam("subitem") SubItem sub){
		clickedSubItem = sub;
	}


	public static class Item {
		private String name;
		List<SubItem> subItems;

		public Item(String name) {
			this.name = name;
			subItems = new ArrayList<SubItem>();
			subItems.add(new SubItem(name+"-sub1"));
			subItems.add(new SubItem(name+"-sub2"));
		}
		
		public List<SubItem> getSubItems(){
			return subItems;
		}

		public String getName() {
			return name;
		}
	}

	public static class SubItem {
		private String name;

		public SubItem(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}
