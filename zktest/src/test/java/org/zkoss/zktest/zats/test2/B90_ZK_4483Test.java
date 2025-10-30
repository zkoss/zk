/* B90_ZK_4483Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar 16 12:19:44 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B90_ZK_4483Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		testMask();
	}

	@Test
	public void testStackup() {
		connect(getTestURL("B90-ZK-4483-stackup.zul"));
		testMask();
	}

	private void testMask() {
		eval("window.scrollTo(0, document.body.scrollHeight)");
		waitResponse();

		JQuery mask = jq(".z-modal-mask");
		Assertions.assertEquals("fixed", mask.css("position"));

		Assertions.assertEquals(
				jq("@window:last").positionTop(),
				jq(".z-apply-mask").positionTop(), 2, "Busy mask not is covered");
	}
}
