/* F50_2923266Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 08 16:52:13 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_2923266Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@colorbox"));
		waitResponse();
		Assert.assertTrue(jq(".z-colorbox-popup").isVisible());

		click(jq("@button"));
		waitResponse();

		click(jq("@colorbox"));
		waitResponse();
		Assert.assertFalse(jq(".z-colorbox-popup").isVisible());
	}
}
