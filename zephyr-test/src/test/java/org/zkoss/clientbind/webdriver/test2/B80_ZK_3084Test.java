/* B80_ZK_3084Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Feb 24 14:12:46 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B80_ZK_3084Test extends ClientBindTestCase {
	@Test
	public void test() {
		try {
			connect();
			JQuery buttons = jq("@button");
			click(buttons.eq(0));
			waitResponse();
			click(buttons.eq(1));
			waitResponse();
			click(buttons.eq(2));
			waitResponse();
		} catch (Exception e) {
			fail();
		}
		assertNoJSError();
	}
}
