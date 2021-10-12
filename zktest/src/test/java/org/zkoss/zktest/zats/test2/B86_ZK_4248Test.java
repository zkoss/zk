package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4248Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		jq(".z-listbox-body").scrollTop(1000);
		waitResponse();
		Assert.assertFalse(isZKLogAvailable());
	}
}
