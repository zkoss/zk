/* B50_2949947Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 21 12:29:11 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.matchesRegex;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2949947Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@textbox"));
		sendKeys(jq("@textbox:eq(0)"), Keys.TAB);
		sendKeys(jq("@timebox:eq(0) input"), "111111");
		Assert.assertEquals("11:11:11", jq("@timebox:eq(0) input").val());

		sendKeys(jq("@timebox:eq(0) input"), Keys.TAB);
		sendKeys(jq("@timebox:eq(1) input"), "11");
		Assert.assertThat(jq("@timebox:eq(1) input").val(), matchesRegex("^[AP]M 11:.*"));
	}
}
