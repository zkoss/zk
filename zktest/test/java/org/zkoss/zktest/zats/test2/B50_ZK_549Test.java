package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B50_ZK_549Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery gbs = jq("@groupbox");
		for (int i = 0; i < 12; i++) {
			JQuery gb = gbs.eq(i);
			Assert.assertEquals("Height should be 200px", jq(gb).outerHeight(), 200);
			JQuery header = gb.find(".z-groupbox-title");
			if (!header.exists()) {
				header = jq(gb).find(".z-caption").eq(0);
			}
			if (header.exists()) {
				click(header);
				waitResponse();
				click(header);
				waitResponse();
				assertEquals("Height should be 200px", jq(gb).outerHeight(), 200);
			}
		}
	}
}
