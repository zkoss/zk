/* B102_ZK_5889ChildPojo.java

	Purpose:

	Description:

	History:
		Fri Mar 28 11:51:21 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

public class B102_ZK_5889ChildPojo {
	private String name = "";
	private int age;

	public B102_ZK_5889ChildPojo() {
	}

	public B102_ZK_5889ChildPojo(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getDebugString() {
		return "ChildPojo{" + "name='" + name + '\'' + ", age=" + age + '}';
	}
}
