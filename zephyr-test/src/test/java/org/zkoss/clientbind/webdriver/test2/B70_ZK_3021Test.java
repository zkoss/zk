/* B70_ZK_3021Test.java

	Purpose:
		
	Description:
		
	History:
		5:28 PM 12/25/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
@Disabled
public class B70_ZK_3021Test extends ClientBindTestCase {
	@Test
	public void testZK3021() {
		connect();
		sleep(2000);
		JQuery labels = jq("@label");
		assertEquals(11, labels.length());
		for (int i = 1; i < 11; i += 2) {
			assertEquals(labels.eq(i).text(), labels.eq(i + 1).text());
		}
	}
}
