package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B35_2436434Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		int x = jq("$tabs").scrollLeft();
		click(jq(".z-tabbox").toWidget().$n("right"));
		waitResponse();
		int y = jq("$tabs").scrollLeft();
		assertTrue(x == 0);
		assertTrue(y > 0);
    }
}
