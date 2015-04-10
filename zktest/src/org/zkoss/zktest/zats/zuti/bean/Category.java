package org.zkoss.zktest.zats.zuti.bean;

public class Category {
	private String name;
	
	// used for proxy to access
	public Category() {}
	
	public Category(String name) {
		this.name = name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	public int hashCode() {
		String name = getName();
		if (name == null) {
			return 0;
		}
		return name.hashCode();
	}
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Category) {
			String oname = ((Category) obj).getName();
			String name = getName();
			if (oname == null)
				return name == oname;
			else
				return oname.equals(name);
		}
		return false;
	};
}
