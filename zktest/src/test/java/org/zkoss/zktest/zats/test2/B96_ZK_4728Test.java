/* B96_ZK_4728Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 10 16:14:12 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B96_ZK_4728Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final Widget item2 = widget("$parent");
		click(item2.$n("open"));
		waitResponse();
		assertNoJSError();
		Assertions.assertFalse(jq("$child1").isVisible());
		Assertions.assertFalse(jq("$child2").isVisible());

		click(item2.$n("open"));
		waitResponse();
		assertNoJSError();
		Assertions.assertFalse(jq("$child1").isVisible());
		Assertions.assertTrue(jq("$child2").isVisible());
	}
}
