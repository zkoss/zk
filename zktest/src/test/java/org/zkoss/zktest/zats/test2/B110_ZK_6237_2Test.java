/* B110_ZK_6237_2Test.java

	Purpose:

	Description:

	History:
		Mon Nov 02 11:14:58 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.zktest.zats.TabletWebDriverTestCase;

@ForkJVMTestOnly
public class B110_ZK_6237_2Test extends TabletWebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("true", getEval("'' + zk.tabletUIEnabled"), "tablet UI must be on");
		int width = jq("$tb").outerWidth();
		click(jq("$tb input"));
		waitResponse();
		click(jq("@label"));
		waitResponse();
		sleep(500); // the inplace look comes back after InputWidget's 150ms timer
		assertTrue(jq("$tb").hasClass("z-timebox-inplace"), "tb should be back in the inplace look");
		assertEquals(width, jq("$tb").outerWidth(), 1, "tb should keep the hflex width after editing");
	}
}
