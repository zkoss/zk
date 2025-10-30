package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B00651 {

	boolean check = true;
	String name ="A";
	int age = 10;
	double price = 10.35678234234D;
	
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public String getName() {
		return name;
	}
	
	@NotifyChange
	public void setName(String name) {
		this.name = name;
	}
	
	public int getAge() {
		return age;
	}
	
	@NotifyChange
	public void setAge(int age) {
		this.age = age;
	}
	public double getPrice() {
		return price;
	}
	@NotifyChange
	public void setPrice(double price) {
		this.price = price;
	}
	@Command @NotifyChange("check")
	public void cmd1(){
		check = !check;
		System.out.println(">>>>"+check);
	}
}
