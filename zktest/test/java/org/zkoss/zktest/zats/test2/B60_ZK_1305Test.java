/* B60_ZK_1305Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 12 16:42:55 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.containsString;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.touch.TouchActions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B60_ZK_1305Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect();

		JQuery test0 = jq(".z-listitem:contains(test0)");
		click(test0);
		waitResponse();

		Assert.assertEquals("select index: 0", jq("$lbl").text());

		Point test0Loc = toElement(test0).getLocation();
		new TouchActions(driver)
				.down(test0Loc.x + 5, test0Loc.y + 5)
				.singleTap(toElement(test0))
				.move(20, 200)
				.up(20, 200)
				.perform();
		waitResponse();
		Assert.assertThat("Should be able to drag test0 item.", jq("$ghostInfo").text(), containsString("z-drop-text"));
	}
}
