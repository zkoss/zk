package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
