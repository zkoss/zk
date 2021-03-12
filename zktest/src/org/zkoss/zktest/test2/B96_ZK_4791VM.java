package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.util.Clients;

public class B96_ZK_4791VM {
	@Command
	public void doClick() {
		Clients.log("Clicked");
	}
}
