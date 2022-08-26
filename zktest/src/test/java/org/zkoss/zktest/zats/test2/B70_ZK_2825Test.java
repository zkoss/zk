package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B70_ZK_2825Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("0\n1\n2\n", jq("$lbl").text());
	}
}
