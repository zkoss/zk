/* B110_ZK_6216Test.java

		Purpose:

		Description:

		History:
				Tue Nov 10 11:20:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6216Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(d -> "true".equals(getEval("window.zk6216Loaded === true")));
		sleep(1000);

		assertTrue(Integer.parseInt(getEval("window.zk6216Before[1][0]"))
				<= Integer.parseInt(getEval("window.zk6216Before[1][1]")),
				"the second tab strip should fit before the font loads");
		for (int i = 0; i < 2; i++) {
			JQuery tabs = jq("@tabs").eq(i);
			assertTrue(tabs.scrollWidth() > Integer.parseInt(getEval("window.zk6216Before[" + i + "][0]")),
					"the web font should make the tabs wider");
			assertTrue(tabs.find(".z-tab").last().hasClass("z-tab-selected"));
			assertEquals(tabs.scrollWidth() - tabs.outerWidth(), tabs.scrollLeft(), 2, "tabbox " + i);
		}

		JQuery tabs = jq("@tabs").eq(0);
		click(jq("@tabbox").eq(0).find(".z-tabbox-left-scroll"));
		sleep(1000);
		int scrollLeft = tabs.scrollLeft();
		assertTrue(scrollLeft < tabs.scrollWidth() - tabs.outerWidth() - 2, "the left arrow should scroll the strip");
		click(jq("@button"));
		new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(d -> "true".equals(getEval("window.zk6216OtherLoaded === true")));
		sleep(1000);
		assertEquals(scrollLeft, tabs.scrollLeft(), "a font no tab uses must not move the tab strip");
	}
}
