/* F96_ZK_4918Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 07 15:10:15 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author rudyhuang
 */
public class F96_ZK_4918Test extends WebDriverTestCase {
	@ClassRule
	public static ExternalZkXml CONFIG = new ExternalZkXml(F96_ZK_4918Test.class);

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect();

		Assert.assertEquals("false", jq("$touchEnabled").text());

		final Widget win = widget("@window");
		final int oldPositionTop = jq(win).positionTop();
		final int oldPositionLeft = jq(win).positionLeft();
		dragdropTo(win.$n("cap"), 0, 0, 100, 100);
		Assert.assertEquals(oldPositionTop, jq(win).positionTop());
		Assert.assertEquals(oldPositionLeft, jq(win).positionLeft());
	}
}

