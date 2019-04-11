/* B50_2925671Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 12:48:05 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;

/**
 * @author rudyhuang
 */
public class B50_2925671Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Element btn = widget("@combobox").$n("btn");
		click(btn);
		sleep(100);
		click(btn);
		waitResponse(true);

		Assert.assertFalse("Should be close correctly", jq(".z-combobox-popup").isVisible());
	}
}
