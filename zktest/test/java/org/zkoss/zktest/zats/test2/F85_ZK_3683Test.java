/* F85_ZK_3683Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 19 12:28:07 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F85_ZK_3683Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery splitter = jq("@splitlayout .z-splitlayout-splitter");
		JQuery win = jq("@window");
		int win1H = win.eq(1).height();
		int win2H = win.eq(2).height();

		getActions().moveToElement(toElement(splitter), 2, 2)
				.clickAndHold()
				.moveByOffset(0, -50)
				.release()
				.perform();
		waitResponse();
		Assert.assertEquals(win1H - 50, win.eq(1).height(), 2);
		Assert.assertEquals(win2H + 50, win.eq(2).height(), 2);

		click(splitter.find(".z-splitlayout-splitter-button"));
		waitResponse();
		Assert.assertFalse("Window 1 should be hidden", win.eq(1).isVisible());

		click(splitter.find(".z-splitlayout-splitter-button"));
		waitResponse();
		Assert.assertTrue("Window 1 should be visible", win.eq(1).isVisible());

		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("There are 4 windows now. (including outer window)", win.length(), 4);
	}
}
