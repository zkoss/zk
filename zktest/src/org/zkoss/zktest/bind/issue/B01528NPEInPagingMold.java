package org.zkoss.zktest.bind.issue;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B01528NPEInPagingMold {

	protected ItemService itemService = new ItemService();
	protected List<Item> itemList = itemService.getAllItems();
	protected Item pickedItem = new Item();

	@NotifyChange({ "pickedItem", "itemList" })
	// move "itemList" in front of "pickedItem" can eliminate
	// NullPointerException
	@Command
	public void add() {
		itemList.add(pickedItem);
		// pickedItem = new Item(); add this statement can eliminate
		// NullPointerException
	}

	@NotifyChange({ "pickedItem", "itemList" })
	// move "itemList" in front of "pickedItem" can eliminate
	// IndexOutOfBoundsException
	@Command
	public void delete() {
		int index = itemList.indexOf(pickedItem);
		if (index != -1) {
			itemList.remove(index);
			pickedItem = new Item();
			// pickedItem = null; replace new Item() with null can eliminate
			// IndexOutOfBoundsException
		}

	}

	public List<Item> getItemList() {
		return itemList;
	}

	public Item getPickedItem() {
		return pickedItem;
	}

	public void setPickedItem(Item pickedItem) {
		this.pickedItem = pickedItem;
	}

	static class ItemService {

		private LinkedList<Item> itemList = new LinkedList<Item>();
		private static int DEFAULT_SIZE = 10;
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
			if (!(obj instanceof Item))
				return false;
			Item other = (Item) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}