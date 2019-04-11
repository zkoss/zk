/* B85_ZK_3821Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec 18 12:42:38 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3821Test extends WebDriverTestCase {
	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.addArguments("--lang=de-DE")
				.setExperimentalOption("prefs", Collections.singletonMap("intl.accept_languages", "de-DE"));
	}

	@Test
	public void test() {
		connect();
		sleep(1500);

		click(jq("@button"));
		waitResponse();

		Assert.assertThat(
				"The message is still in English!",
				jq(".z-messagebox > @label").html(),
				not(startsWith("The resource you request")));
	}
}
