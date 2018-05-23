package org.zkoss.zktest.test2;

import java.io.Serializable;

public class B85_ZK_3733_Food implements Serializable {
	private static final long serialVersionUID = 1L;
	private String category;
	private String name;
	private String topNutrients;
 
	public B85_ZK_3733_Food(String category, String name, String topNutrients) {
		this.category = category;
		this.name = name;
		this.topNutrients = topNutrients;
	}
 
	public String getCategory() {
		return category;
	}
 
	public void setCategory(String category) {
		this.category = category;
	}
 
	public String getName() {
		return name;
	}
 
	public void setName(String name) {
		this.name = name;
	}
 
	public String getTopNutrients() {
		return topNutrients;
	}
 
	public void setTopNutrients(String topNutrients) {
		this.topNutrients = topNutrients;
	}
 
}
