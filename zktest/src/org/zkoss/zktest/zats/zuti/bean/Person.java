/** Person.java.

	Purpose:
		
	Description:
		
	History:
		3:32:32 PM Nov 19, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.bean;

import java.io.Serializable;

/**
 * @author jumperchen
 */
public class Person implements Serializable{
	private String name, address;

	public Person(String name, String address) {
		this.name = name;
		this.address = address;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public String toString() {
		return name + "@" + address;
	}
}
