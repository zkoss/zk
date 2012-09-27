package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class B01347CheckboxCrash {
	List<Product> productList = new ArrayList<Product>();
	private int count = 0;
	
	Product selected;
	
	@Init
	public void init() {
		for (int i = 0; i < 3; i++) {
			productList.add(mock());
		}
	}
	
	public Product getSelected() {
		return selected;
	}

	public void setSelected(Product selected) {
		this.selected = selected;
	}


	public List<Product> getList() {
		return productList;
	}

	@Command
	@NotifyChange("*")
	public void delete() {
		productList.remove(0);
	}

	public Product mock() {
		Product result = new Product();
		result.setName("name " + count);
		count++;
		return result;
	}
	
	public class Product {
		


		private String name;
		private boolean flag;


		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isFlag() {
			return flag;
		}

		public void setFlag(boolean flag) {
			this.flag = flag;
		}
	}
}