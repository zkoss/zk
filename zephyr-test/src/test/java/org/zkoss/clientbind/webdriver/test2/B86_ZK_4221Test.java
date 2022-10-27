package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B86_ZK_4221Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery cb = jq("@checkbox");
		try {
			click(cb);
			waitResponse();
			click(cb);
			waitResponse();
		} catch (Exception e) {
			fail();
		}
		assertNoJSError();
	}
}
