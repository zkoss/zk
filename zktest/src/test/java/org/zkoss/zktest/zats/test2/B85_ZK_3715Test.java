package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author bob peng
 */
public class B85_ZK_3715Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-button"));
		waitResponse();
		assertFalse(jq(".z-messagebox-error").exists(), "error popped");
	}
}