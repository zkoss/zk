package org.zkoss.zktest.bind.databinding.el;

public class MethodsVM {
	private String value = "my-test-value";
	private int price1 = 14;
	private int price2 = 0;
	private String[] array = {"array v1", "array v2", "array v3"};
	private String picture = null;
	private int age = 18;

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String concat(String s1, String s2) {
		return s1.concat(s2);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getPrice1() {
		return price1;
	}

	public void setPrice1(int price1) {
		this.price1 = price1;
	}

	public String[] getArray() {
		return array;
	}

	public int getPrice2() {
		return price2;
	}

	public void setPrice2(int price2) {
		this.price2 = price2;
	}
}
