/* B102_ZK_5889Pojo.java

	Purpose:

	Description:

	History:
		Fri Mar 28 11:51:21 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

public class B102_ZK_5889Pojo {
	private String name = "";
	private int age;

	private B102_ZK_5889ChildPojo child;

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

	//	@Immutable
	public B102_ZK_5889ChildPojo getChild() {
		return child;
	}

	public void setChild(B102_ZK_5889ChildPojo child) {
		this.child = child;
	}

	public String getDebugString() {
		return "MyPojo{" + "name='" + name + '\'' + ", age=" + age + '}' + " Child=" + child;
	}
}
