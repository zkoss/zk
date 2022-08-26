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
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
@NotThreadSafe
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

		Assertions.assertEquals("de_DE", jq("@label:last").text());

		click(jq("@button"));
		waitResponse();

		assertThat(
				"The message is still in English!",
				jq(".z-messagebox > @label").html(),
				not(startsWith("The resource you request")));
	}
}
