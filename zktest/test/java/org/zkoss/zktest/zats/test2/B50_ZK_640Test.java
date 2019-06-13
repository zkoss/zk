package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_ZK_640Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery tbbOne = jq("$tbbOne");
		JQuery tbbTwo = jq("$tbbTwo");

		assertFalse("no extra space if image only", tbbOne.html().contains("&nbsp;"));
		assertTrue("space exists between label and image", tbbTwo.html().contains("&nbsp;"));
	}
}
