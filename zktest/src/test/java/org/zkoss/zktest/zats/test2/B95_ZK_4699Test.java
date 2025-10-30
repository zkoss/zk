/* B95_ZK_4699Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Dec 04 14:31:38 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B95_ZK_4699Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery listitems = jq("@listitem");
		JQuery textboxs = jq("@textbox");

		click(listitems.eq(0));
		waitResponse();
		Assertions.assertTrue(listitems.eq(0).hasClass("z-listitem-focus"),
				"first listitem should have focus style");
		click(textboxs.eq(3));
		waitResponse();
		Assertions.assertFalse(listitems.eq(0).hasClass("z-listitem-focus"),
				"first listitem should not have focus style");
		Assertions.assertTrue(textboxs.eq(3).is(":focus"),
				"4th textbox should be focused");
	}
}
