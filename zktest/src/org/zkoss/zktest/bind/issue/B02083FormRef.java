package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;


public class B02083FormRef {

	private Product product;

	public B02083FormRef() {
		product = new Product(new Item("AAA"));
	}
	
	
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public static class Item {
		private String name;
		private String name1;
		private String name2;

		public Item(String name) {
			this.name = name;
			this.name1 = name+"1";
			this.name2 = name+"2";
		}

		public String getName() {
			return name;
		}

		public String getName1() {
			return name1;
		}

		public String getName2() {
			return name2;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setName1(String name1) {
			this.name1 = name1;
		}

		public void setName2(String name2) {
			this.name2 = name2;
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
	
	@Command
	public void save(){
		
	}
}
