package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B30_1826606Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		checkAlign();
		click(jq("@button"));
		waitResponse();
		checkAlign();
		click(jq("@button"));
		waitResponse();
		checkAlign();
	}

	public void checkAlign() {
		JQuery listHead = jq("@listhead");
		JQuery items = jq("@listitem");
		int colLength = listHead.children().length();
		int rowLength = items.length();
		for (int i = 0; i < colLength; i++) {
			for (int j = 0; j < rowLength; j++) {
				assertTrue(Math.abs(listHead.children().eq(i).width() - items.eq(j).children().eq(i).width()) <= 1);
			}
		}
	}
}