/* B96_ZK_4857Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 19 11:35:47 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.containsString;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B96_ZK_4857Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.addArguments("user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/89.0.4389.90 Mobile/15E148 Safari/604.1");
		// fake as a mobile to have touch support desktop
	}

	@Test
	public void test() {
		connect();

		final JQuery target = jq("@div");
		rightClick(target);
		waitResponse();
		Assert.assertTrue(jq("@menupopup").isVisible());

		dblClick(target);
		waitResponse();
		Assert.assertTrue(isZKLogAvailable());
		MatcherAssert.assertThat(getZKLog(), containsString("onDoubleClick"));
	}
}
