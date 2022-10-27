package org.zkoss.clientbind.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B86_ZK_4211Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		JQuery cb = jq(".z-listitem-checkbox").eq(2);
		click(cb);
		waitResponse();
		click(cb);
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
