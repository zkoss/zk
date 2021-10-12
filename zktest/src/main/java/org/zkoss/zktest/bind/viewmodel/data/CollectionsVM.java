/* CollectionsVM.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 18:20:40 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private Map<Integer, Address> addressMap;
	private int addressMapIndexCounter;

	public List<Address> getAddresses() {
		return addresses;
	}

	public Set<Address> getAddressSet() {
		return addressSet;
	}

	public Map<Integer, Address> getAddressMap() {
		return addressMap;
	}

	@Init
	public void init() {
		addresses = new ArrayList<>();
		addresses.add(new Address("NY", "7th Avenue"));
		addresses.add(new Address("LA", "8th Street"));

		addressSet = new HashSet<>();
		addressSet.add(new Address("NY", "7th Avenue"));
		addressSet.add(new Address("LA", "8th Street"));

		addressMap = new HashMap<>();
		addressMap.put(1, new Address("NY", "7th Avenue"));
		addressMap.put(2, new Address("LA", "8th Street"));
		addressMapIndexCounter = 3;
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
	@NotifyChange("addressMap")
	public void removeMap(@BindingParam int key) {
		addressMap.remove(key);
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

	@Command
	@NotifyChange("addressMap")
	public void addMap() {
		addressMap.put(addressMapIndexCounter++, getRandomAddress());
	}

	private Address getRandomAddress() {
		return new Address("ZK", System.currentTimeMillis() + " Avenue");
	}
}
