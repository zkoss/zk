/* F80_ZK_2951Test.java

	Purpose:
		
	Description:
		
	History:
		12:36 PM 11/12/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class F80_ZK_2951Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery buttons = jq("@button");
		click(buttons.eq(0));
		waitResponse();
		assertNoAnyError();
		click(buttons.eq(1));
		waitResponse();
		assertNoAnyError();

		click(buttons.eq(2));
		waitResponse();
		assertTrue(hasError());
	}
}
