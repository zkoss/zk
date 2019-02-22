package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

public class B85_ZK_3680Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery tabJq = jq(".z-tabpanel").eq(0);
		tabJq.scrollTop((int) Math.round(tabJq.scrollHeight() * 0.8));
		waitResponse();

		click(jq(".z-bandbox-button"));
		waitResponse(true);

		assertEquals("Did a thing\nopened", getZKLog());

		tabJq.scrollTop(0);
		waitResponse(true);

		assertEquals("Did a thing\nopened\nDid a thing\nclosed", getZKLog());
	}
}
