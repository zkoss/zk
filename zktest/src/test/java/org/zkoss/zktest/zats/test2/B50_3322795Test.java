package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

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
			assertEquals((value + ".7"), ds.toWidget().$n("real").get("value"), "the value change is 1 each time");
		}

		for (int j = 1; j <= 10; j++) {
			value -= 1;
			click(ds.toWidget().$n("btn-down"));
			waitResponse();
			assertEquals((value + ".7"), ds.toWidget().$n("real").get("value"), "the value change is 1 each time");
		}
    }
}
