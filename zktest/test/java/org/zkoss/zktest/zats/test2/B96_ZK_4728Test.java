/* B96_ZK_4728Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 10 16:14:12 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Widget;

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
		Assert.assertFalse(jq("$child1").isVisible());
		Assert.assertFalse(jq("$child2").isVisible());

		click(item2.$n("open"));
		waitResponse();
		assertNoJSError();
		Assert.assertFalse(jq("$child1").isVisible());
		Assert.assertTrue(jq("$child2").isVisible());
	}
}
