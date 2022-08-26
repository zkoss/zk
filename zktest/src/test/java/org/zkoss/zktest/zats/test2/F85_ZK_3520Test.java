/* F85_ZK_3520Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 19 11:22:36 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F85_ZK_3520Test extends WebDriverTestCase {
	private JQuery pp = jq("@popup");
	private JQuery yellow = jq(".z-div[style*=yellow]");
	private JQuery pink = jq(".z-div[style*=pink]");
	private JQuery cyan = jq(".z-div[style*=cyan]");

	@Test
	public void testBeforeChange() {
		connect();

		rightClick(yellow);
		waitResponse();
		// should see tooltip showed on 50px left of mouse pointer
		Assertions.assertEquals(yellow.offsetTop() + 100, pp.offsetTop(), 1);
		Assertions.assertEquals(yellow.offsetLeft() + 50, pp.offsetLeft(), 1);
		getActions().moveToElement(toElement(yellow), 50, 50).contextClick().perform();
		waitResponse();
		// should see tooltip showed
		Assertions.assertTrue(pp.isVisible(), "yellowPopup should be visible");

		click(pink);
		waitResponse();
		// should see tooltip showed on 20px down of mouse pointer
		Assertions.assertEquals(pink.offsetTop() + 120, pp.offsetTop(), 3);
		Assertions.assertEquals(pink.offsetLeft() + 100, pp.offsetLeft(), 3);

		mouseOver(cyan);
		sleep(500);
		// should see tooltip showed at position "before_start"
		Assertions.assertEquals(cyan.offsetTop() + 200, pp.offsetTop(), 1);
		Assertions.assertEquals(cyan.offsetLeft(), pp.offsetLeft(), 1);
	}

	@Test
	public void testAfterChange() {
		connect();

		//after change
		click(jq("@button"));
		waitResponse();

		rightClick(yellow);
		waitResponse();
		getActions().moveToElement(toElement(yellow), 50, 50).contextClick().perform();
		waitResponse();
		// should not see tooltip showed
		Assertions.assertFalse(pp.isVisible(), "yellowPopup should be hidden");

		click(pink);
		waitResponse();
		// should see tooltip showed at the "after_center" position
		Assertions.assertEquals(pink.offsetTop() + 200, pp.offsetTop(), 3);
		Assertions.assertEquals(pink.offsetLeft() + 100 - pp.width() / 2, pp.offsetLeft(), 3);

		mouseOver(cyan);
		sleep(500);
		// should see tooltip showed at 40px right of the cursor
		Assertions.assertEquals(cyan.offsetTop() + 100, pp.offsetTop(), 1);
		Assertions.assertEquals(cyan.offsetLeft() + 140, pp.offsetLeft(), 1);
	}
}
