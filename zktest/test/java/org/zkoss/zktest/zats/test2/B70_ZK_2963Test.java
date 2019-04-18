/* B70_ZK_2963Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 10:25:59 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2963Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		ChromeOptions options = super.getWebDriverOptions();
		// iPad
		options.addArguments("user-agent=Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1");
		return options;
	}

	@Test
	public void test() {
		connect();

		click(jq("a:contains(Silvertail)"));
		sleep(1000);
		new WebDriverWait(driver, 3)
				.until(ExpectedConditions.presenceOfElementLocated(By.className("z-page")));
		Assert.assertEquals("current theme: silvertail", trim(jq("@label:last").text()));
		Assert.assertEquals(Color.fromString("#eaeaea"), Color.fromString(jq("@window").css("backgroundColor")));
	}
}
