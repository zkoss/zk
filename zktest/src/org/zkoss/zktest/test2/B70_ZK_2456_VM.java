package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.util.Clients;

public class B70_ZK_2456_VM {
	private String[] items = { "Demo", "Edit", "Add" };

	public void setItems(String[] items) {
		this.items = items;
	}

	public String[] getItems() {
		return this.items;
	}

	@Command
	public void selected(@BindingParam("item") String selected) {
		Clients.log("Selected: " + selected);
	}
}