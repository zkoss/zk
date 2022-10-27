package org.zkoss.clientbind.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

public class B86_ZK_4248Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		jq(".z-listbox-body").scrollTop(1000);
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
