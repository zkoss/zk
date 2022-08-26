package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B86_ZK_4354Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals(jq("$hd").width(), jq("$hdn").width());
		assertEquals(jq("$vd").height(), jq("$vdn").height());
	}
}
