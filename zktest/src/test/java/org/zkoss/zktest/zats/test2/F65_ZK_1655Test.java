/* F65_ZK_1655Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 14 12:07:50 CST 2019, Created by rudyhuang

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
public class F65_ZK_1655Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery yellow = jq(".z-div[style*=yellow]");
		rightClick(yellow);
		waitResponse();
		JQuery yellowPP = jq("@popup");
		// should see tooltip showed on 50px left of mouse pointer
		Assert.assertEquals(yellowPP.positionTop(), yellow.offsetTop() + 100, 1);
		Assert.assertEquals(yellowPP.positionLeft() + 50, yellow.offsetLeft() + 100, 1);

		JQuery pink = jq(".z-div[style*=pink]");
		click(pink);
		waitResponse();
		JQuery pinkPP = jq("@popup");

		// should see tooltip showed on 20px down of mouse pointer
		Assert.assertEquals(pinkPP.offsetTop() - 20, pink.offsetTop() + 100, 3);
		Assert.assertEquals(pinkPP.offsetLeft(), pink.offsetLeft() + 100, 3);

		JQuery cyan = jq(".z-div[style*=cyan]");
		mouseOver(cyan);
		waitResponse();
		sleep(1000);
		JQuery cyanPP = jq("@popup");

		// should see tooltip showed on 40px right of mouse pointer
		Assert.assertEquals(cyanPP.offsetTop(), cyan.offsetTop() + 100, 1);
		Assert.assertEquals(cyanPP.offsetLeft() - 40, cyan.offsetLeft() + 100, 1);
	}
}
