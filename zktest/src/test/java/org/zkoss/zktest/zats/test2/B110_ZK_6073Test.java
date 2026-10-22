/* B110_ZK_6073Test.java

        Purpose:

        Description:

        History:
                Wed May 06 20:22:03 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6073Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();

		click(jq("$cb .z-combobox-button"));
		waitResponse(true);
		click(jq("$cb input"));
		// the onChanging timer armed by the first keyup fires while the second Down is held
		getActions().sendKeys(Keys.ARROW_DOWN).keyDown(Keys.ARROW_DOWN)
				.pause(Duration.ofMillis(600)).keyUp(Keys.ARROW_DOWN)
				.pause(Duration.ofMillis(500)).perform();
		waitResponse();

		assertEquals("1", jq("$totalCount").text());
		assertEquals("0", jq("$nonSelectBackCount").text());
	}
}
