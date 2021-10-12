package org.zkoss.zktest.bind.comp;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class TabboxDynamicSelectionVM {

	List<Item> tabs = new ArrayList<Item>();

	Item selectedItem;
	int selectedIndex;

	public TabboxDynamicSelectionVM() {
		tabs.add(new Item("Tab 0"));
		tabs.add(new Item("Tab 1"));
		tabs.add(new Item("Tab 2"));
		tabs.add(new Item("Tab 3"));
		tabs.add(new Item("Tab 4"));

		selectedItem = tabs.get(selectedIndex = 2);
	}

	public List<Item> getTabs() {
		return tabs;
	}

	public void setTabs(List<Item> tabs) {
		this.tabs = tabs;
	}

	public Item getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Item selectedItem) {
		this.selectedItem = selectedItem;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	@Command @NotifyChange("selectedIndex")
	public void selectTabByIndex(@BindingParam("index") Integer index){
		if(index!=null){
			selectedIndex = index;
		}
	}
	
	@Command @NotifyChange("selectedItem")
	public void selectTabByItem(@BindingParam("item") Item item){
		selectedItem = item;
	}

	static public class Item {
		String name;

		public Item(String name) {
			super();
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
