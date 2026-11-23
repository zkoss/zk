package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_4982Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$selectButton"));
		waitResponse();
		Assert.assertFalse(jq("#zk_err").exists());
	}
}
