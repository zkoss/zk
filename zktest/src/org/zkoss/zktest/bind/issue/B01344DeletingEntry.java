package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class B01344DeletingEntry {
	List<Product> productList = new ArrayList<Product>();

	@Init
	public void init() {
		for (int i = 0; i < 10; i++) {
			productList.add(mock());
		}
	}

	public List<Product> getList() {
		return productList;
	}

	@Command
	@NotifyChange("*")
	public void delete() {
		productList.remove(0);
	}

	private int SERIAL = 0;

	public synchronized Product mock() {
		Product result = new Product();
		result.setId("" + SERIAL);
		result.setCategory("category " + SERIAL);
		result.setName("name " + SERIAL);
		SERIAL++;
		return result;
	}
	
	public class Product {


		private String id;
		private String name;
		private String category;
		private boolean flag;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public boolean isFlag() {
			return flag;
		}

		public void setFlag(boolean flag) {
			this.flag = flag;
		}
	}
}