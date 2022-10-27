package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B86_ZK_4146Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		click(jq("@button"));
		waitResponse();
		JQuery dirtyFlag = jq("@window $flag");
		assertEquals("false", dirtyFlag.text());
	}
}
