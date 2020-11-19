package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;

public class B95_ZK_4723VM {
	private ListModelList<String> keys;

	private ListModelList<String> debug;

	@Init
	public void init() {
		this.keys = new ListModelList<>();
		this.debug = new ListModelList<>();
		this.keys.add("one");
		this.keys.add("two");
	}

	public ListModelList<String> getKeys() {
		return this.keys;
	}

	public ListModelList<String> getDebug() {
		return this.debug;
	}

	public String loadValue(String key) {
		Clients.log("Method 'loadValue' called with: " + key);
		return key == null ? null : key.toUpperCase();
	}
}