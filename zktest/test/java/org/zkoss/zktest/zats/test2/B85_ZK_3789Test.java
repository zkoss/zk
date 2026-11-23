package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3789Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("a.z-bandbox-button").get(0));
		waitResponse();
		assertFalse(isZKLogAvailable());
	}
}