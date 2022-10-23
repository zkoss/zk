package org.zkoss.zephyr.test.mvvm.book.databinding.clientbinding;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.util.Clients;

@ToServerCommand("hello")
@ToClientCommand("notifyClient")
@NotifyCommand(value = "notifyClient", onChange = "_vm_.count")
public class ClientBindingVM {
	private int count = 0;

	@Command
	public void hello(@BindingParam("data") String value) {
		Clients.log("hello " + value);
	}

	@Command
	public void sayHello() {
		Clients.evalJavaScript("hello()");
	}

	@Command
	@NotifyChange("count")
	public void add() {
		count++;
	}

	@Command
	public void pureAdd() {
		count++;
	}

	public int getCount() {
		return count;
	}
}
