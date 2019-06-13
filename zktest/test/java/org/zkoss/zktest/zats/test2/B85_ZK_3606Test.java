package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B85_ZK_3606Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery window = jq(".z-window-content");
		JQuery button = jq("button");
		JQuery test = jq(".z-hlayout").find(".z-label");
		click(test);
		waitResponse();
		JQuery popup = jq(".z-popup-open").first();
		waitResponse();
		int popupTop1 = popup.offsetTop();
		window.scrollTop(100);
		waitResponse();
		int popupTop2 = popup.offsetTop();
		assertEquals(100, popupTop1 - popupTop2);
		waitResponse();
		window.scrollTop(0);
		waitResponse();
		click(button);
		waitResponse();
		JQuery noti = jq(".z-notification").first();
		int noti1 = noti.offsetTop();
		window.scrollTop(100);
		waitResponse();
		int noti2 = noti.offsetTop();
		assertEquals(100, noti1 - noti2);
	}
}
