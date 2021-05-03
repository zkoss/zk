/* Address.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 18:21:05 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.data;

/**
 * @author rudyhuang
 */
public class Address {
	private String city;
	private String street;

	public Address() {
	}

	public Address(String city, String street) {
		this.city = city;
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Override
	public String toString() {
		return String.format("%s, %s", city, street);
	}
}