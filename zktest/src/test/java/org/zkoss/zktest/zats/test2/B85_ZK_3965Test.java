/* B85_ZK_3965Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 15 12:57:53 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3965Test extends WebDriverTestCase {
	@Test
	public void testModel() {
		connect();

		assertTrue(isSelected(jq("@radio:contains(s1)")), "s1 not selected");

		click(jq("@button:contains(add s3 to model)"));
		waitResponse();
		assertTrue(jq("@radiogroup").find("@radio:contains(s3)").exists(),
				"s3 not attached");
	}

	@Test
	public void testExternal() {
		connect();

		assertTrue(isSelected(jq("@radio:contains(e6)")), "e6 not selected");

		click(jq("@button:contains(add an external radio)"));
		waitResponse();
		assertFalse(isSelected(jq("@radio:contains(e6)")), "e6 still selected");
		assertTrue(isSelected(jq("@radio:last")), "new radio not selected");
	}

	private boolean isSelected(JQuery radio) {
		return radio.find("> :radio").is(":checked");
	}
}
