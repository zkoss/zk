package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01640CommandSend2WrongViewModelTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery showChildBtn = jq("$showChildBtn");

		click(showChildBtn);
		waitResponse();
		JQuery lab = jq("$workContent $childWin $lab");
		assertEquals("initialized", lab.text());

		JQuery outerBtn = jq("$workContent $childWin $outerBtn");
		click(outerBtn);
		waitResponse();
		assertEquals("do outerGridCommand", lab.text());

		JQuery innerBtn = jq("$workContent $childWin $innerBtn");
		click(innerBtn);
		waitResponse();
		assertEquals("do innerGridCommand A", lab.text());
	}
}
