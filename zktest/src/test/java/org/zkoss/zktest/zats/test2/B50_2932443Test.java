/* B50_2932443Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 14:32:12 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2932443Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.addArguments("--lang=de-DE")
				.setExperimentalOption("prefs", Collections.singletonMap("intl.accept_languages", "de-DE"));
	}

	@Test
	public void test() {
		connect();

		String col1Val = jq("@grid:eq(0) @decimalbox:eq(0)").val();
		String col3Val = jq("@grid:eq(0) @doublebox:eq(0)").val();
		String serverVal = jq("@grid:eq(0) @textbox:eq(0)").val();
		Assert.assertEquals(serverVal, col1Val);
		Assert.assertEquals(serverVal, col3Val);

		String v1 = jq("@grid:eq(1) @decimalbox:eq(0)").val();
		Assert.assertEquals("€ 39824,00", v1);
		String v2 = jq("@grid:eq(1) @doublebox:eq(0)").val();
		Assert.assertEquals("€ 39824,00", v2);
		String v3 = jq("@grid:eq(1) @decimalbox:eq(1)").val();
		Assert.assertEquals("€ 39.824,00", v3);
		String v4 = jq("@grid:eq(1) @doublebox:eq(1)").val();
		Assert.assertEquals("€ 39.824,00", v4);
	}
}
