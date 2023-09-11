/* B100_ZK_5035Test.java

		Purpose:

		Description:

		History:
				Fri Sep 08 17:14:11 CST 2023, Created by rebeccalai

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B100_ZK_5035Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		int checkableBefore = jq(".z-listitem-checkable").length();
		click(jq("@button"));
		waitResponse();
		int checkableAfter = jq(".z-listitem-checkable").length();
		assertEquals(checkableBefore, checkableAfter);
		jq(".z-listitem-checkable").forEach(item -> assertFalse(item.parent().parent().prev().exists()));
	}
}
