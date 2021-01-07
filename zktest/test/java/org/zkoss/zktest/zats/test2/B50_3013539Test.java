/* B50_3013539Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 07 15:04:38 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_3013539Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("$tc"));
		waitResponse();
		click(jq("@button[label=\"disabled\"]"));
		waitResponse();
		click(jq("@button[label=\"change label\"]"));
		waitResponse();
		Assert.assertEquals("ABC", jq("$tc").text());

		click(jq("@button[label=\"disabled\"]"));
		waitResponse();
		Assert.assertFalse(jq("@treeitem").hasClass("z-treerow-disabled"));
	}
}
