package org.zkoss.clientbind.test.book.viewmodel.data;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class MapVM {
	private Map<String, Address> addressMap;
	private int addressMapIndexCounter;

	public Map<String, Address> getAddressMap() {
		return addressMap;
	}

	@Init
	public void init() {
		addressMap = new HashMap<>();
		addressMap.put("zzz", new Address("NY", "7th Avenue"));
		addressMap.put("bbb", new Address("LA", "8th Street"));
		addressMapIndexCounter = 3;
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
	@NotifyChange("addressMap")
	public void addMap() {
		addressMap.put(addressMapIndexCounter++ + "", getRandomAddress());
	}

	private Address getRandomAddress() {
		return new Address("ZK", System.currentTimeMillis() + " Avenue");
	}
}
