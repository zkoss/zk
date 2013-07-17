package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

public class B01848FormBindingUpdate {

	private List<Item> list;
	private Product product;

	public B01848FormBindingUpdate() {
		list = new ArrayList<Item>();
		list.add(new Item("Item 1"));
		list.add(new Item("Item 2"));
		list.add(new Item("Item 3"));
		product = new Product(null);
	}
	
	public String getXname(){
		return "name";
	}

	public List getList() {
		return list;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public static class Item {
		private String name;

		public Item(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static class Product {
		private Item item;

		public Product(Item item) {
			this.item = item;
		}

		public Item getItem() {
			return item;
		}

		public void setItem(Item item) {
			this.item = item;
		}
	}
}
