/* B95_ZK_4738Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Dec 15 14:42:14 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B95_ZK_4738Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
			.setExperimentalOption("mobileEmulation", Collections
				.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		Actions act = new Actions(connect());
		act.clickAndHold(toElement(jq(".box").eq(0))).moveToElement(toElement(jq(".box").eq(2))).release().perform();
		waitResponse();
		Assert.assertTrue("onDrop should be triggered", jq("@toast").exists());
	}
}
