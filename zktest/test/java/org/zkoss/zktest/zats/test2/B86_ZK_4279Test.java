package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4279Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("$chgBtn"));
		waitResponse();
		click(jq("$show"));
		waitResponse();
		click(jq("$chgBtn"));
		waitResponse();
		click(jq("$show"));
		waitResponse();
		click(jq("$logBtn"));
		waitResponse();
		Assert.assertEquals("1", getZKLog());
	}
}
