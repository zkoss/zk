package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author bob peng
 */
public class B85_ZK_3732Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery btn = jq("$btn");
		click(btn);
		waitResponse();
		assertFalse(jq(".z-messagebox-error").exists(), "error popped");
	}
}