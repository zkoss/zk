package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



/**
 * @author bob peng
 */
public class B85_ZK_3723Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("password", jq(".z-combobox-input:eq(0)").attr("type"), "Input type should be password");
		assertEquals("password", jq(".z-bandbox-input:eq(0)").attr("type"), "Input type should be password");
	}
}