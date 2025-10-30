/* B70_ZK_2804Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 14:35:53 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B70_ZK_2804Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@listitem:eq(0)"));
		waitResponse();

		JQuery body = jq(".z-listbox-body");
		body.scrollTop(body.scrollHeight());
		waitResponse();

		getActions().keyDown(Keys.SHIFT)
				.click(toElement(jq("@listitem:last")))
				.keyUp(Keys.SHIFT)
				.perform();
		waitResponse();

		body.scrollTop(0);
		waitResponse();

		Assertions.assertTrue(jq("@listitem:eq(0)").hasClass("z-listitem-selected"));
	}
}
