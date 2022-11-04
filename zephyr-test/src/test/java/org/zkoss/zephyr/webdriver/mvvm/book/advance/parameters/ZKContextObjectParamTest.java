/* ZKContextObjectParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 17:07:21 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.advance.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class ZKContextObjectParamTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery bindContextString = jq("$bindContextString");
		JQuery binderString = jq("$binderString");
		JQuery eventName = jq("$eventName");
		JQuery cmdName = jq("$cmdName");
		JQuery executionString = jq("$executionString");
		JQuery idSpaceString = jq("$idSpaceString");
		JQuery viewString = jq("$bindViewString");
		JQuery componentString = jq("$bindComponentString");
		JQuery pageString = jq("$pageString");
		JQuery desktopString = jq("$desktopString");
		JQuery sessionString = jq("$sessionString");
		JQuery webAppName = jq("$webAppName");

		assertNotEquals("", bindContextString.text());
		assertNotEquals("", binderString.text());
		assertEquals("null", eventName.text());
		assertEquals("null", cmdName.text());
		assertNotEquals("", executionString.text());
		assertNotEquals("", idSpaceString.text());
		assertNotEquals("", viewString.text());
		assertNotEquals("", componentString.text());
		assertNotEquals("", pageString.text());
		assertNotEquals("", desktopString.text());
		assertNotEquals("", sessionString.text());
		assertNotEquals("", webAppName.text());

		click(jq("@button"));
		waitResponse();

		assertNotEquals("", bindContextString.text());
		assertNotEquals("", binderString.text());
		assertEquals("onClick", eventName.text());
		assertEquals("show", cmdName.text());
		assertNotEquals("", executionString.text());
		assertNotEquals("", idSpaceString.text());
		assertNotEquals("", viewString.text());
		assertNotEquals("", componentString.text());
		assertNotEquals("", pageString.text());
		assertNotEquals("", desktopString.text());
		assertNotEquals("", sessionString.text());
		assertNotEquals("", webAppName.text());
	}
}
