package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Default;
import org.zkoss.zk.ui.util.Clients;

public class B96_ZK_4889VM {
	@Command
	public void test1(@BindingParam("number") @Default("-1") int number) {
		Clients.log("test1 param - number: " + number);
	}

	@Command
	public void test2(@Default("-1") int number) {
		Clients.log("test2 param - number: " + number);
	}

	@Command
	public void test3(int count, @Default("-1") int number) {
		Clients.log("test3 param - count: " + count + ", param - number: " + number);
	}
}
