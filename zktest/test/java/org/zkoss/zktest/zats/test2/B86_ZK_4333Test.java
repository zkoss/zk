/* B86_ZK_4333Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 15 15:25:41 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author rudyhuang
 */
public class B86_ZK_4333Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final Widget cb = widget("@combobox");
		final Element button = cb.$n("btn");
		click(button);
		waitResponse();
		click(button);
		waitResponse();
		final int hFlexMinWidth = jq(cb.$n()).width();

		click(button);
		waitResponse();
		click(button);
		waitResponse();
		Assert.assertEquals(hFlexMinWidth, jq(cb.$n()).width(), 2);
	}
}
