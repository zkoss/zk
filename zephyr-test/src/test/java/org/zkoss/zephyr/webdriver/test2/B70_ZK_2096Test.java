package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B70_ZK_2096Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery btn = jq(".z-button");
		for (int i = 0; i < 5; i++) {
			click(btn);
			waitResponse();
		}
		assertFalse(jq("[id$=hdfaker-bar] + [id$=hdfaker]").exists(), "the z-treecols-bar should not be in front of treecol");
	}
}