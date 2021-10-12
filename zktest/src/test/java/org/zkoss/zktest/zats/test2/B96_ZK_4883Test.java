/* B96_ZK_4883Test.java

	Purpose:
		
	Description:
		
	History:
		Wed May 26 15:10:16 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B96_ZK_4883Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		assertNoJSError();
		final Alert alert = new WebDriverWait(driver, 3000).until(ExpectedConditions.alertIsPresent());
		Assert.assertEquals("onsubmit triggered", alert.getText());

		alert.accept();
		new WebDriverWait(driver, 3000).until(ExpectedConditions.urlContains("B96-ZK-4883-redirect.zul"));
		final String result = jq("$result").text();
		MatcherAssert.assertThat(result, Matchers.containsString("xmlContent: blahblah"));
		MatcherAssert.assertThat(result, Matchers.containsString("encoding: utf-8"));
	}
}
