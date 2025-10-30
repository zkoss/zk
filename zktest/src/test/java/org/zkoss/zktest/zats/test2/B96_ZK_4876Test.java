/* B96_ZK_4876Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 02 11:58:45 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4876Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String assertMessage = "The descendant component of the Columnlayout shall not receive \"white-space: no-wrap;\" from Columnlayout";
		Assertions.assertNotEquals(assertMessage, "nowrap", jq("$cc").css("white-space"));
		Assertions.assertNotEquals(assertMessage, "nowrap", jq("$l1").css("white-space"));
		Assertions.assertNotEquals(assertMessage, "nowrap", jq("$l2").css("white-space"));
		Assertions.assertNotEquals(assertMessage, "nowrap", jq("$l3").css("white-space"));
	}
}
