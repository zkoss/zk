/* CollectionsVM.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 18:20:40 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.mvvm.book.viewmodel.data;

import java.util.*;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class CollectionsVM {
	private List<Address> addresses;
	private Set<Address> addressSet;
	public List<Address> getAddresses() {
		return addresses;
	}

	public Set<Address> getAddressSet() {
		return addressSet;
	}

	@Init
	public void init() {
		addresses = new ArrayList<>();
		addresses.add(new Address("NY", "7th Avenue"));
		addresses.add(new Address("LA", "8th Street"));

		addressSet = new HashSet<>();
		addressSet.add(new Address("NY", "7th Avenue"));
		addressSet.add(new Address("LA", "8th Street"));
	}

	@Command
	@NotifyChange("addresses")
	public void removeList(@BindingParam Address item) {
		addresses.remove(item);
	}

	@Command
	@NotifyChange("addressSet")
	public void removeSet(@BindingParam Address item) {
		addressSet.remove(item);
	}

	@Command
	public void viewData(@BindingParam Address item) {
		Clients.log(item);
	}

	@Command
	@NotifyChange("addresses")
	public void addList() {
		addresses.add(getRandomAddress());
	}

	@Command
	@NotifyChange("addressSet")
	public void addSet() {
		addressSet.add(getRandomAddress());
	}

	private Address getRandomAddress() {
		return new Address("ZK", System.currentTimeMillis() + " Avenue");
	}
}
