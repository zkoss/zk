/* B86_ZK_4017Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 06 17:19:29 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4017Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(doFilter by clear/add)"));
		waitResponse();
		Assertions.assertNotEquals(0, jq("@row").length());

		click(jq("@button:contains(doClear by clear/add)"));
		waitResponse();
		Assertions.assertNotEquals(0, jq("@row").length());
	}

	@Test
	public void testListbox() {
		connect(getTestURL("B86-ZK-4017-listbox.zul"));

		click(jq("@button:contains(doFilter by clear/add)"));
		waitResponse();
		Assertions.assertNotEquals(0, jq("@listitem").length());

		click(jq("@button:contains(doClear by clear/add)"));
		waitResponse();
		Assertions.assertNotEquals(0, jq("@listitem").length());
	}
}
