/* B96_ZK_4853Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 20 12:18:50 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B96_ZK_4853Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		assertNoJSError();

		final JQuery treeBody = jq(widget("@tree").$n("body"));
		treeBody.scrollTop(treeBody.scrollHeight()); // to the bottom
		waitResponse();
		Assertions.assertTrue(jq("@checkbox").isVisible(), "Checkbox invisible");

		click(jq("@checkbox"));
		waitResponse();
		Assertions.assertTrue(isZKLogAvailable(), "Event is not sent");
	}
}
