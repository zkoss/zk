/* F50_3299827Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 18:45:54 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_3299827Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		getActions().keyDown(Keys.COMMAND)
				.click(toElement(jq("@listitem:eq(0)")))
				.click(toElement(jq("@listitem:eq(2)")))
				.click(toElement(jq("@listitem:eq(4)")))
				.click(toElement(jq("@listitem:eq(6)")))
				.keyUp(Keys.COMMAND)
				.perform();
		waitResponse();
		Assert.assertEquals(4, jq("@listitem").filter(".z-listitem-selected").length());
	}
}
