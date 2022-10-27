/* F86_ZK_4028Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 16 17:39:07 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

/**
 * @author rudyhuang
 */
public class F86_ZK_4028Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);

		// Navigation
		click(jq("@button[label^='Direct NavTo']"));
		waitResponse();
		assertEquals(1, jq("@label[value='Level 3']").length());
		assertEquals(1, jq("@label[value='Level 3']").length());

		click(jq("@a[label='Maintenance']"));
		waitResponse();
		assertEquals(1, jq("@label[value='Maintenance']").length());

		click(jq("@button[label='NavTo Configuration']"));
		waitResponse();
		assertEquals(1, jq("@label[value='Level 1']").length());
		assertEquals(1, jq("@label[value='Configuration']").length());
		assertEquals(0, jq("@label[value='Level 2']").length());

		// Insert / Remove
		click(jq("@button[label^='InsertBefore']"));
		waitResponse();
		assertEquals(1, jq("@a[label='New Item']").length());

		click(jq("@button[label^='InsertBefore']"));
		waitResponse();
		assertTrue(hasError());

		click(jq("@a[label='Diagnostics']"));
		waitResponse();
		click(jq("@button[label='Remove Diagnostics']"));
		waitResponse();
		assertEquals(0, jq("@a[label='Diagnostics']").length());
		assertEquals(1, jq("@label[value='Maintenance']").length());

		click(jq("@button[label='Remove Maintenance']"));
		waitResponse();
		assertEquals(0, jq("@a[label='Maintenance']").length());
		assertEquals(1, jq("@label[value='New Item']").length());

		// Test serialize/deserialize
		click(jq("@button[label^='Direct NavTo']"));
		waitResponse();
		assertEquals(1, jq("@label[value='Level 3']").length());
		assertEquals(1, jq("@label[value='AAA Profiles']").length());
		click(jq("@button[label='Test serialize/deserialize']"));
		waitResponse();
		click(jq("@a[label='Auth Servers']"));
		waitResponse();
		assertEquals(1, jq("@label[value='Level 3']").length());
		assertEquals(1, jq("@label[value='Auth Servers']").length());
	}
}
