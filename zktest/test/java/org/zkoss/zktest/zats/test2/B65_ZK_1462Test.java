/* B65_ZK_1462Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 10 12:12:56 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B65_ZK_1462Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(1200); // renderdefer="1000"
		assertNoJSError();

		JQuery panel1 = jq("@panel:contains(1)").find(".z-panel-header");
		JQuery panel2 = jq("@panel:contains(2)").find(".z-panel-header");
		Assert.assertTrue("Panel 1 should be visible", panel1.isVisible());
		Assert.assertTrue("Panel 2 should be visible", panel2.isVisible());

		JQuery firstPortal = jq(".z-portalchildren:eq(0)");
		Actions actions = getActions();
		actions.dragAndDropBy(toElement(panel2), panel1.positionLeft() - panel2.positionLeft(), 0).perform();
		Assert.assertEquals("Panel 2 should be moved to first portalchildren",
				toElement(firstPortal), toElement(panel2.parents(".z-portalchildren")));

		actions.dragAndDropBy(toElement(panel1), 0, panel2.positionTop() - panel1.positionTop()).perform();
		Assert.assertEquals("Panel 1 should be the first child in the first portalchildren",
				toElement(firstPortal.find("@panel:first").find(".z-panel-header")), toElement(panel1));
	}
}
