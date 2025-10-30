/* BasicVM.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 16:39:27 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.data;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class BasicVM {
	private int index;
	private double price;
	private String name;
	private Address address;

	@Init
	public void init() {
		index = 7;
		price = 399.99;
		name = "Potix";
		address = new Address("NY", "7th Avenue");
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Command
	public void showProperties() {
		Clients.log(String.format("ID[%d] Price[%.2f] Name[%s] Address[%s]", index, price, name, address));
	}
}
