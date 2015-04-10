package org.zkoss.zktest.zats.zuti.vm;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.impl.BinderUtil;

public class NavigationViewModel {

	private List<Item> data = new LinkedList<Item>();
	private boolean multiple;
	private boolean verticle;

	@Init
	public void init() {
		data.add(new Item("Home"));
		data.add(new Item("Services"));
		data.add(new Item("About"));
		data.add(new Item("Contact us"));
		multiple = true;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setVerticle(boolean verticle) {
		this.verticle = verticle;
	}
	public boolean isVerticle() {
		return verticle;
	}
	public void setData(List<Item> data) {
		this.data = data;
	}
	public List<Item> getData() {
		return this.data;
	}
	public void setSelectedItems(List<Item> items) {}
	public List<Item> getSelectedItems() {
		List<Item> selected = new LinkedList<Item>();
		for (Item item : data) {
			if (item.isActive())
				selected.add(item);
		}
		return selected;
	}
	
	@Command
	@NotifyChange({"multiple", "selectedItems"})
	public void changeMultiple() {
		multiple = !multiple;
		// reset
		for (Item i : data) {
			i.setActive(false);
		}
		BindUtils.postNotifyChange(null, null, data, ".");
	}
	
	@Command
	@NotifyChange("verticle")
	public void changeOrient() {
		verticle = !verticle;
	}
	
	@Command
	@NotifyChange("selectedItems")
	public void doActive(@BindingParam("item") Item item) {
		if (multiple) {
			item.setActive(!item.isActive());
			BindUtils.postNotifyChange(null, null, item, ".");
		} else {
			if (!item.isActive()) {
				for (Item i : data) {
					i.setActive(false);
				}
			}
			item.setActive(!item.isActive());
			BindUtils.postNotifyChange(null, null, data, ".");
		}
	}
	
	public static class Item {
		private String label;
		private boolean active;
		public Item(String label) {
			this.label = label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public String getLabel() {
			return label;
		}
		public void setActive(boolean isActive) {
			this.active = isActive;
		}
		public boolean isActive() {
			return this.active;
		}
	}
}
