package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B70_ZK_2622Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse(3000);
		String result = "hello\n</Script>";
		assertEquals(result, getZKLog().trim());
		JQuery btn = jq("@button");
		click(btn);
		waitResponse(true);
		result = "hello\n</Script>\nhello\n</Script>";
		assertEquals(result, getZKLog().trim());
	}
}