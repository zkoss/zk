package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B86_ZK_4223Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		waitResponse();
		JQuery g = jq("@grid");
		Assert.assertEquals(g.parent().innerWidth(), g.outerWidth());
	}
}
