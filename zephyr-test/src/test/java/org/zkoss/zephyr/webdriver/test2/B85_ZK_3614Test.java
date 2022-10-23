package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author bob peng
 */
public class B85_ZK_3614Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		JQuery btnOpen = jq("$btnOpen");
		JQuery btnAdd = jq("$btnAdd");
		JQuery close = jq(".z-window-close");
		click(btnOpen);
		waitResponse();
		click(close);
		waitResponse();
		click(btnAdd);
		waitResponse();
		click(btnOpen);
		waitResponse();
		assertFalse(jq(".z-messagebox-error").exists(), "error popped");
	}
}