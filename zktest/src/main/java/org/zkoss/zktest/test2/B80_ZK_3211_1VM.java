package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

public class B80_ZK_3211_1VM {
	private String label1 = "my label1";
	public String getLabel1() { return label1; }

	@NotifyChange("label1")
	@Command
	public void updateLabel1() {
		label1 += ".";
		Clients.showNotification("inner command triggered (1)");
	}
}
