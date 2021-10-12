package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_3322795Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery ds = jq("@doublespinner");
		int value = 8;
		for (int i = 1; i <= 5; i++) {
			value += 1;
			click(ds.toWidget().$n("btn-up"));
			waitResponse();
			assertEquals("the value change is 1 each time",
					(value + ".7"), ds.toWidget().$n("real").get("value"));
		}

		for (int j = 1; j <= 10; j++) {
			value -= 1;
			click(ds.toWidget().$n("btn-down"));
			waitResponse();
			assertEquals("the value change is 1 each time",
					(value + ".7"), ds.toWidget().$n("real").get("value"));
		}
    }
}
