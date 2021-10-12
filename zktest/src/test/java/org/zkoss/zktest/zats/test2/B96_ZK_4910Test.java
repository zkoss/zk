/* B96_ZK_4910Test.java

	Purpose:
		
	Description:
		
	History:
		Thu May 27 16:19:13 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;

/**
 * @author rudyhuang
 */
public class B96_ZK_4910Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect();
		waitResponse();

		final Element treeBody = widget("@tree").$n("body");
		jq(treeBody).scrollTop(5000);
		waitResponse();
		jq(treeBody).scrollTop(10000);
		waitResponse();
		Assert.assertEquals(10000, jq(treeBody).scrollTop());
	}
}
