package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B50_3151694Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery db = jq("@decimalbox");
		sendKeys(db, "25-");
		waitResponse();
		blur(db);
		waitResponse();
		assertTrue(jq(".z-errorbox-content").exists());
		click(jq("@button"));
		waitResponse();
		assertFalse(jq("@window[title=\"ZK Test\"] @label").text().contains("NumberFormatException"));
	}
}
