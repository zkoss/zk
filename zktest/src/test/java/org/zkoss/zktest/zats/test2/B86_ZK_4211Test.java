package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B86_ZK_4211Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery cb = jq(".z-listitem-checkbox").eq(2);
		click(cb);
		waitResponse();
		click(cb);
		waitResponse();
		Assert.assertFalse(isZKLogAvailable());
	}
}
