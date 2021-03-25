package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

public class B96_ZK_4791VM {
	private int count = 0;

	@Command
	@NotifyChange("count")
	public void doClick() {
		Clients.log("Clicked");
		count++;
	}

	public int getCount() {
		return count;
	}
}
