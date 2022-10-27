/* B80_ZK_3611Test.java

	Purpose:
		
	Description:
		
	History:
		Mon, Apr 24, 2017  2:45:22 PM, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B80_ZK_3611Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery button = jq("@button");
		click(button);
		waitResponse();
		JQuery label = jq("@label").eq(2);
		assertEquals("24", label.text());
	}
}