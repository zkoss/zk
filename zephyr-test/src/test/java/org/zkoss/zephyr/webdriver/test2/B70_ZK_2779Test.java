package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B70_ZK_2779Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String msg_1 = "selection count after unchecked is 22";
		String msg_2 = msg_1 + "\nselection count after unchecked is 11";
		String msg_3 = msg_2 + "\nselection count after unchecked is 0";
		String msg_4 = msg_3 + "\nselection count after unchecked is 11";
		click(jq(".z-treerow-checkbox:eq(0)"));
		waitResponse();
		assertEquals(msg_1, getZKLog());
		click(jq(".z-treerow-checkbox:eq(1)"));
		waitResponse();
		assertEquals(msg_2, getZKLog());
		click(jq(".z-treerow-checkbox:eq(2)"));
		waitResponse();
		assertEquals(msg_3, getZKLog());
		click(jq(".z-treerow-checkbox:eq(2)"));
		waitResponse();
		assertEquals(msg_4, getZKLog());
	}
}
