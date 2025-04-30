package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.util.Clients;

public class B102_ZK_5904VM {
	@Command
	public void doTest_1_2(@BindingParam int a) {
		Clients.log("doCommand -> " + a);
	}

	@Command
	public void doTest_3_4(@BindingParam int a, @BindingParam int b) {
		Clients.log("doCommand -> " + a + ", " + b);
	}
}
