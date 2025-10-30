package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F86_ZK_3963Test extends WebDriverTestCase {
	@Test
	public void test1() {
		connect();
		eval("window.scrollTo(0, 1000)");
		waitResponse();
		JQuery getSelectedItem = jq(".z-button:contains(getSelectedItem())");
		JQuery getItemCount = jq(".z-button:contains(getItemCount())");
		JQuery getVisibleItemCount = jq(".z-button:contains(getVisibleItemCount())");
		click(jq(".z-button:contains(setSelectedItem(item3))"));
		waitResponse();
		clickAndVerifyLog(getSelectedItem, "item3");

		click(findButtonByContent("setSelectedItem(null)"));
		waitResponse();
		clickAndVerifyLog(getSelectedItem, "null");

		click(findNodeByContent("item1"));
		waitResponse();
		assertEquals("onSelect: item1", getZKLog());
		closeZKLog();
		waitResponse();
		clickAndVerifyLog(getSelectedItem, "item1");
		clickAndVerifyLog(getItemCount, "9");
		clickAndVerifyLog(getVisibleItemCount, "9");

		click(findNodeByContent("item3").find(".z-orgnode-icon"));
		waitResponse();
		clickAndVerifyLog(getItemCount, "9");
		clickAndVerifyLog(getVisibleItemCount, "4");

		click(findButtonByContent("clear()"));
		waitResponse();
		clickAndVerifyLog(getItemCount, "0");
	}

	@Test
	public void test2() {
		connect();
		eval("window.scrollTo(0, 1000)");
		JQuery getSelectedItem = jq(".z-button:contains(getSelectedItem())");
		JQuery getVisibleItemCount = jq(".z-button:contains(getVisibleItemCount())");
		JQuery isOpen = jq(".z-button:contains(isOpen)");
		JQuery isSelected = jq(".z-button:contains(isSelected)");

		testItem3Selectable(findButtonByContent("setDisabled"));
		testItem3Selectable(findButtonByContent("setSelectable"));

		click(findButtonByContent("appendChild"));
		waitResponse();
		assertTrue(findNodeByContent("newItem").exists());

		click(findButtonByContent("removeChild"));
		waitResponse();
		assertFalse(findNodeByContent("newItem").exists());

		clickAndVerifyLog(isOpen, "true");
		click(findButtonByContent("setOpen"));
		waitResponse();
		clickAndVerifyLog(isOpen, "false");
		clickAndVerifyLog(getVisibleItemCount, "4");

		clickAndVerifyLog(isSelected, "false");
		click(findButtonByContent("setSelected"));
		waitResponse();
		clickAndVerifyLog(isSelected, "true");
		clickAndVerifyLog(getSelectedItem, "item3");

		clickAndVerifyLog(findButtonByContent("getLevel"), "1");
		clickAndVerifyLog(findButtonByContent("getParentItem"), "item1");
		clickAndVerifyLog(findButtonByContent("isContainer"), "true");
		clickAndVerifyLog(findButtonByContent("isEmpty"), "false");
	}

	private void testItem3Selectable(JQuery button) {
		click(button);
		waitResponse();

		click(findNodeByContent("item3"));
		waitResponse();

		clickAndVerifyLog(jq(".z-button:contains(isSelected)"), "false");

		click(button);
		waitResponse();
	}

	@Test
	public void test3() {
		connect();
		eval("window.scrollTo(0, 1000)");
		JQuery getSelectedItem = jq(".z-button:contains(getSelectedItem())");
		JQuery getItemCount = jq(".z-button:contains(getItemCount())");
		JQuery isOpen = jq(".z-button:contains(isOpen)");
		JQuery isSelected = jq(".z-button:contains(isSelected)");
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
		clickAndVerifyLog(isSelected, "true");
		clickAndVerifyLog(getSelectedItem, "item3");

		click(findButtonByContent("clearSelection"));
		waitResponse();
		clickAndVerifyLog(isSelected, "false");
		clickAndVerifyLog(getSelectedItem, "null");

		click(findButtonByContent("open3"));
		waitResponse();
		clickAndVerifyLog(isOpen, "true");
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
