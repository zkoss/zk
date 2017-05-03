package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class B85_ZK_3643Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery btn = jq("@button");
		click(btn.eq(0));
		waitResponse();
		click(btn.eq(0));
		waitResponse();

		JQuery listheaders = jq("th.z-listheader");
		JQuery hdfakers = jq("col[id$=hdfaker]");
		int listheadersLength = listheaders.length();
		int hdfakersLength = hdfakers.length();
		assertTrue("List header count must > 0", listheadersLength > 0);
		assertEquals("<th> and <col> get out of synch", listheadersLength, hdfakersLength);
	}
}
