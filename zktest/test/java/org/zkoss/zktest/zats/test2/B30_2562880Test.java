/* B30_2562880Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 22 11:44:55 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B30_2562880Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@listitem"));
		waitResponse();
		Assert.assertTrue(jq("$popup").isVisible());

		click(jq(".z-listheader-sorticon"));
		waitResponse();
		Assert.assertFalse(jq("$popup").isVisible());
	}
}
