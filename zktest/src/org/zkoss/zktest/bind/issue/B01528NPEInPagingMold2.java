package org.zkoss.zktest.bind.issue;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B01528NPEInPagingMold2 {

	protected ItemService itemService = new ItemService();
	protected List<Item> itemList = itemService.getAllItems();

	@Command
	public void notifyItemChange(@BindingParam("index") int index) {
		Item item = itemList.get(index);
		item.setName(item.getName() + " Updated");
		BindUtils.postNotifyChange(null, null, itemList, "["+index+"]");
	}


	public List<Item> getItemList() {
		return itemList;
	}

	static class ItemService {

		private LinkedList<Item> itemList = new LinkedList<Item>();
		private static int DEFAULT_SIZE = 25;
		private int id = 0;

		public ItemService(int maxSize) {
			for (int i = 0; i < maxSize; i++) {
				itemList.add(new Item("Item " + i));
			}
		}

		public ItemService() {
			this(DEFAULT_SIZE);
		}

		public LinkedList<Item> getAllItems() {
			return itemList;
		}

		public void add(Item item) {
			itemList.add(item);
		}

		public void delete(Item item) {
			int index = itemList.indexOf(item);
			if (index != -1) {
				itemList.remove(index);
			}
		}
	}

	static public class Item {

		private String name;

		public Item() {

		}

		public Item(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}