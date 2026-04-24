/* B110_ZK_6090Test.java

        Purpose:

        Description:

        History:
                Wed May 06 16:42:55 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.zktest.zats.TabletWebDriverTestCase;

@ForkJVMTestOnly
public class B110_ZK_6090Test extends TabletWebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();

		// without the tablet UI the zkmax touch molds never load and this test
		// would exercise the CE fallback instead of zkmax.layout.Cardlayout
		assertEquals("true", jq("$touchEnabled").text());
		assertEquals("true", jq("$tabletUI").text());
		// zk.Widget already declares bindSwipe_, so only an own property on the
		// Cardlayout prototype proves the cardlayout-touch augment really loaded
		assertEquals("true", getEval(
				"Object.prototype.hasOwnProperty.call(zkmax.layout.Cardlayout.prototype, 'bindSwipe_')"),
				"the cardlayout-touch augment must be installed");
		assertEquals(3, jq("@tab").length());

		// close test3 tab (index 2), whose panel contains <cardlayout>
		click(widget("@tab:eq(2)").$n("cls"));
		waitResponse();

		assertEquals(2, jq("@tab").length());
		assertNoAnyError();
		assertNoJSError();
	}
}
