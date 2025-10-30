package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

public class B80_ZK_3211_2VM {
	private String label2 = "my label2";
	public String getLabel2() { return label2; }

	@NotifyChange("label2")
	@Command
	public void updateLabel2() {
		label2 += ".";
		Clients.showNotification("inner command triggered (2)");
	}
}
