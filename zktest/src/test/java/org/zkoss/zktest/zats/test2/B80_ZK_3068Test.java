package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B80_ZK_3068Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(3000);
		JQuery tbtn = jq("$tbtn");
		String zcf = "zk.currentFocus.uuid";
		assertEquals(getEval(zcf), tbtn.attr("id"));
		sendKeys(tbtn, Keys.TAB);
		waitResponse();
		assertEquals(getEval(zcf), jq("$btn1").attr("id"));
	}
}
