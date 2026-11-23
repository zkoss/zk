/* B70_ZK_2977Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 16:52:47 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2977Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		eval("window.scrollTo(0, document.body.scrollHeight)");
		waitResponse();
		click(widget("@combobox:last").$n("btn"));
		waitResponse();

		Assert.assertTrue(jq("@combobox:last").offsetTop() < jq(".z-combobox-popup:visible").offsetTop());

		eval("window.scrollBy(0, -100)");
		waitResponse();
		Assert.assertTrue(jq("@combobox:last").offsetTop() < jq(".z-combobox-popup:visible").offsetTop());
	}
}
