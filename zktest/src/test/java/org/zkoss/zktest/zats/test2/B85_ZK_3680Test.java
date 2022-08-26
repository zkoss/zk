package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;



public class B85_ZK_3680Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery tabJq = jq(".z-tabpanel").eq(0);
		tabJq.scrollTop(jq("@bandbox").positionTop());
		waitResponse();

		click(jq(".z-bandbox-button"));
		waitResponse(true);

		assertEquals("Did a thing\nopened", getZKLog());

		tabJq.scrollTop(0);
		waitResponse(true);

		assertEquals("Did a thing\nopened\nDid a thing\nclosed", getZKLog());
	}
}
