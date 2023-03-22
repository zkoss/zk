package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F86_ZK_3963Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery getItemCount = jq(".z-button:contains(getItemCount())");
		click(findButtonByContent("remove3"));
		waitResponse();
		assertFalse(findNodeByContent("item3").exists());
		clickAndVerifyLog(getItemCount, "3");

		click(findButtonByContent("add3"));
		waitResponse();
		assertTrue(findNodeByContent("item3").exists());
		clickAndVerifyLog(getItemCount, "4");

		click(findButtonByContent("select3"));
		waitResponse();
		assertTrue(findNodeByContent("item3").parent().hasClass("z-orgitem-selected"));

		click(findButtonByContent("clearSelection"));
		waitResponse();
		assertFalse(findNodeByContent("item3").parent().hasClass("z-orgitem-selected"));

		click(findButtonByContent("open3"));
		waitResponse();
		clickAndVerifyLog(getItemCount, "7");
	}

	private JQuery findNodeByContent(String content) {
		return jq(".z-organigram:eq(0) .z-orgnode:contains(" + content + ")");
	}

	private JQuery findButtonByContent(String content) {
		return jq(".z-button:contains(" + content + ")");
	}

	private void clickAndVerifyLog(JQuery target, String log) {
		click(target);
		waitResponse();
		assertEquals(log, getZKLog());
		closeZKLog();
		waitResponse();
	}
}
