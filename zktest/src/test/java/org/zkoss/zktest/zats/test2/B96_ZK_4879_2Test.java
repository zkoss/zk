/* B96_ZK_4879_2Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 25 14:25:00 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

@Tag("WcagTestOnly")
public class B96_ZK_4879_2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		String menuitemSelector = "> [role=\"menuitem\"]";
		
		click(jq("$nav1"));
		waitResponse();
		Assertions.assertTrue(
				jq("$navitem11").find(menuitemSelector).is(":focus"),
				"the focus shall move to the navitem inside");
		
		click(jq("$navitem2"));
		waitResponse();
		Assertions.assertTrue(
				jq("$navitem2").find(menuitemSelector).is(":focus"),
				"the focus shall move to navitem2");
		
		click(jq("$nav1"));
		waitResponse();
		Assertions.assertTrue(
				jq("$navitem11").find(menuitemSelector).is(":focus"),
				"the focus shall move to the navitem inside");
		
		click(jq("$navitem2"));
		waitResponse();
		Assertions.assertTrue(
				jq("$navitem2").find(menuitemSelector).is(":focus"),
				"the focus shall move to navitem2");
		
		click(jq("$nav3"));
		waitResponse();
		Assertions.assertTrue(jq("$nav3").find(menuitemSelector).is(":focus"),
				"the focus shall move to the nav/navitem you clicked.");
		
		click(jq("$navitem4"));
		waitResponse();
		Assertions.assertTrue(
				jq("$navitem4").find(menuitemSelector).is(":focus"),
				"the focus shall move to the nav/navitem you clicked.");
		
		click(jq("$nav5"));
		waitResponse();
		Assertions.assertTrue(jq("$nav5").find(menuitemSelector).is(":focus"),
				"the focus shall move to the nav/navitem you clicked.");
		
		click(jq("$nav6"));
		waitResponse();
		Assertions.assertTrue(jq("$nav6").find(menuitemSelector).is(":focus"),
				"the focus shall move to the nav/navitem you clicked.");
	}
}
