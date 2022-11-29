package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class F80_ZK_3060Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals(6, jq(".z-paging").length());
		assertEquals(4 * 6, jq(".z-paging button:disabled").length()); //6 pagings
		assertEquals(6, jq(".z-paging input:disabled").length());
		click(jq("@button"));
		waitResponse();
		assertEquals(4 * 6, jq(".z-paging button").length()); //6 pagings (total)
		assertEquals(6, jq(".z-paging input").length());
		assertEquals(2 * 6, jq(".z-paging button:disabled").length()); //6 pagings (first + prev)
		assertEquals(0, jq(".z-paging input:disabled").length());
		click(jq("@button"));
		waitResponse();
		assertEquals(4 * 6, jq(".z-paging button:disabled").length()); //6 pagings
		assertEquals(6, jq(".z-paging input:disabled").length());
	}
}