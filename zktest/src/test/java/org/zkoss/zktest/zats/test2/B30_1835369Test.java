package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B30_1835369Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(1));
		waitResponse();
		checkAlign();
		click(jq("@button").eq(2));
		waitResponse();
		checkAlign();
		click(jq("@button").eq(0));
		waitResponse();
		checkAlign();
		click(jq("@button").eq(1));
		waitResponse();
		checkAlign();
		click(jq("@button").eq(2));
		waitResponse();
		checkAlign();
		click(jq("@button").eq(0));
		waitResponse();
		checkAlign();
	}

	public void checkAlign() {
		JQuery cols = jq(".z-columns");
		JQuery rows = jq(".z-rows");
		int colLength = cols.children().length();
		for (int i = 0; i < colLength - 1; i++) { //skip scrollbar
			for (int j = 0; j < 3; j++) {
				assertTrue(
						Math.abs(cols.children().eq(i).width() - rows.children().eq(j).children().eq(i).width()) <= 1);
			}
		}
	}
}