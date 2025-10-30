/* B80_ZK_3340Test.java

		Purpose:
                
		Description:
                
		History:
				Tue Mar 26 17:43:13 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B80_ZK_3340Test extends WebDriverTestCase {

	@Test
	public void test() {
		WebDriver driver = connect();
		JQuery input = jq(".z-bandbox-input");

		verify(() -> {
			click(input);
			waitResponse();
		}, "bandbox focussed:onFocus");

		verify(() -> {
			sendKeys(input, "xxx");
			waitResponse();
		}, "bandbox changing:onChanging");

		verify(() -> {
			click(jq(".z-bandbox-button"));
			waitResponse();
		}, "bandbox focussed:onFocus");

		verify(() -> {
			final WebElement popup = toElement(jq(".z-bandpopup"));
			// To click the pink area inside the bandpopup
			new Actions(driver).moveToElement(popup, popup.getSize().width / 2 - 1, 0).click().build().perform();
			waitResponse();
		}, "bandbox focussed:onFocus");

		verify(() -> {
			click(jq(".z-label"));
			waitResponse();
		}, "bandbox changed:onChange\n" +
				"bandbox blurred:onBlur");
	}

	private void verify(Command command, String log) {
		command.execute();
		Assertions.assertEquals(log, getZKLog());
		closeZKLog();
	}

	@FunctionalInterface
	interface Command {
		void execute();
	}
}
