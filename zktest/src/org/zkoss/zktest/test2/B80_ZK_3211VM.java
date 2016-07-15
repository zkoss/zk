package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

public class B80_ZK_3211VM {
	private String label = "my label";
	public String getLabel() { return label; }

	@NotifyChange("label")
	@Command
	public void updateLabel() {
		label += ".";
		Clients.showNotification("inner command triggered");
	}
}
