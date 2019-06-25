/* B80_ZK_2938Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 13 14:46:07 CST 2019, Created by rudyhuang

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
public class B80_ZK_2938Test extends WebDriverTestCase {
	private static final int DRAG_THRESHOLD = 2;

	@Test
	public void test() {
		connect();

		JQuery w1 = jq("$w1");
		int w1width = w1.outerWidth();
		int w1height = w1.outerHeight();
		getActions().moveToElement(toElement(w1), w1width - DRAG_THRESHOLD, 30)
				.clickAndHold()
				.moveByOffset(-100, 0)
				.release()
				.perform();
		waitResponse();
		Assert.assertEquals(w1width - 100, w1.outerWidth(), 2);

		getActions().moveToElement(toElement(w1), 30, w1height - DRAG_THRESHOLD)
				.clickAndHold()
				.moveByOffset(0, -100)
				.release()
				.perform();
		waitResponse();
		Assert.assertEquals(w1height - 100, w1.outerHeight(), 2);
	}
}
