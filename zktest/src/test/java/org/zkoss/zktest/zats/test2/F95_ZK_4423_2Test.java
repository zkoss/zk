/* F95_ZK_4423_2Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Nov 03 17:18:15 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class F95_ZK_4423_2Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		act.dragAndDrop(toElement(jq(".z-panel-header-move").eq(0)), toElement(jq(".z-panel-header-move").eq(1))).perform();
		waitResponse();
		Assert.assertFalse(isZKLogAvailable());

		act.dragAndDrop(toElement(jq(".z-panel-header-move").eq(2)), toElement(jq(".z-panel-header-move").eq(3))).perform();
		waitResponse();
		Assert.assertEquals("onPortalMove", getZKLog());
		closeZKLog();

		act.dragAndDrop(toElement(jq(".z-panel-header-move").eq(4)), toElement(jq(".z-panel-header-move").eq(5))).perform();
		waitResponse();
		Assert.assertEquals("onPortalDrop", getZKLog());
		closeZKLog();

		act.dragAndDrop(toElement(jq(".z-panel-header-move").eq(6)), toElement(jq(".z-panel-header-move").eq(7))).perform();
		waitResponse();
		Assert.assertEquals("onPortalDrop\n" + "onPortalMove", getZKLog());
		closeZKLog();

		act.dragAndDrop(toElement(jq(".z-panel-header-move").eq(8)), toElement(jq(".z-panel-header-move").eq(9))).perform();
		waitResponse();
		Assert.assertEquals("onPortalDrop\n" + "onPortalMove", getZKLog());
		closeZKLog();
		Assert.assertEquals(1, jq(".z-portalchildren").eq(8).toWidget().nChildren());
		Assert.assertEquals(1, jq(".z-portalchildren").eq(9).toWidget().nChildren());
	}
}
