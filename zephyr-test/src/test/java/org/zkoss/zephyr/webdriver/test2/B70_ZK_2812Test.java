package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B70_ZK_2812Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		//make sure no notification is present at init
		assertFalse(jq(".z-notification").exists());
		//click to open group 1
		click(jq(".z-group-icon").eq(0));
		waitResponse();
		//check notification appears
		assertTrue(jq(".z-notification").exists());
		//click to close group 1
		click(jq(".z-group-icon").eq(0));
		waitResponse();
		//check notification appears
		assertTrue(jq(".z-notification").exists());
		//click to open group 2
		click(jq(".z-group-icon").eq(1));
		waitResponse();
		//check notification appears
		assertTrue(jq(".z-notification").exists());
		//click to close group 2
		click(jq(".z-group-icon").eq(1));
		waitResponse();
		//check notification appears
		assertTrue(jq(".z-notification").exists());
	}
}
