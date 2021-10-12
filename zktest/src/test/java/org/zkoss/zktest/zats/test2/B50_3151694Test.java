package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
