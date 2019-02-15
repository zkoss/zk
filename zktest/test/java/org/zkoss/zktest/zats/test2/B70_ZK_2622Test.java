package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

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