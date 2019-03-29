/* B80_ZK_3340Test.java

		Purpose:
                
		Description:
                
		History:
				Tue Mar 26 17:43:13 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

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
			new Actions(driver).moveToElement(toElement(jq(".z-bandpopup")), 190, 90).click().build().perform();
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
		Assert.assertEquals(log, getZKLog());
		closeZKLog();
	}

	@FunctionalInterface
	interface Command {
		void execute();
	}
}
