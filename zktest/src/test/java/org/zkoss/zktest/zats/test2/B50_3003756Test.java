/* B50_3003756Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 22 12:50:13 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_3003756Test extends WebDriverTestCase {
	private ThreadLocal<String> acceptLang = new ThreadLocal<>();

	@Override
	protected ChromeOptions getWebDriverOptions() {
		String lang = acceptLang.get();
		return super.getWebDriverOptions()
				.addArguments("--lang=" + lang)
				.setExperimentalOption("prefs", Collections.singletonMap("intl.accept_languages", lang));
	}

	@Test
	public void testGerman() {
		acceptLang.set("de-DE");
		connect();

		Assertions.assertEquals("35,00", jq("@doublebox").val());

		click(jq("@button"));
		waitResponse();

		Assertions.assertEquals("100,00", jq("@doublebox").val());
	}

	@Test
	public void testEnglish() {
		acceptLang.set("en-US");
		connect();

		Assertions.assertEquals("35.00", jq("@doublebox").val());

		click(jq("@button"));
		waitResponse();

		Assertions.assertEquals("100.00", jq("@doublebox").val());
	}
}
