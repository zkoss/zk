package org.zkoss.zephyr.webdriver.mvvm.issue;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B01887DetachAttachTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		assertEquals("./B01887DetachAttachInner1.zul", jq("$inc1").find("$lab1").text());
		assertEquals(0, jq("$inc2").length());

		click(jq("$btn2"));
		waitResponse();
		assertEquals("./B01887DetachAttachInner2.zul", jq("$inc2").find("$lab2").text());
		assertEquals(0, jq("$inc1").length());

		click(jq("$btn1"));
		waitResponse();
		assertEquals("./B01887DetachAttachInner1.zul", jq("$inc1").find("$lab1").text());
		assertEquals(0, jq("$inc2").length());
	}
}
