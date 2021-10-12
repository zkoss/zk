/* F70_ZK_2226Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 16:09:19 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F70_ZK_2226Test extends WebDriverTestCase {
	private static final int RESIZE_THRESHOLD = 3;

	@Test
	public void test() {
		connect();

		JQuery panel1 = jq("@panel:contains(Panel 1)");
		int panel1Width = panel1.outerWidth();
		getActions().moveToElement(toElement(panel1))
				.moveByOffset(panel1Width / 2 - RESIZE_THRESHOLD, 0)
				.clickAndHold()
				.moveByOffset(100, 0)
				.release()
				.perform();
		waitResponse();
		int panel1WidthAfter = panel1.outerWidth();
		int panel1HeightAfter = panel1.outerHeight();
		Assert.assertThat(panel1WidthAfter, greaterThan(panel1Width));
		Assert.assertEquals(panel1WidthAfter, jq("@panel:contains(Panel 2)").outerWidth());

		getActions().dragAndDrop(
					toElement(panel1.find(".z-panel-header-move")),
					toElement(jq("@panel:contains(Panel 3)")))
				.perform();
		waitResponse();
		Assert.assertEquals(panel1WidthAfter, panel1.outerWidth());
		Assert.assertEquals(panel1HeightAfter, panel1.outerHeight());
	}
}
