/* B70_ZK_2357Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 14 11:39:13 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B70_ZK_2357Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		final JQuery btn = jq("@button");
		final String btnBackground = getBackground(btn);
		getActions().moveToElement(toElement(btn)).perform();
		Assertions.assertNotEquals(btnBackground, getBackground(btn));

		final JQuery toolbtn = jq("@toolbarbutton");
		final String toolbtnBackground = getBackground(toolbtn);
		getActions().moveToElement(toElement(toolbtn)).perform();
		Assertions.assertNotEquals(toolbtnBackground, getBackground(toolbtn));

		click(jq("@menu"));
		waitResponse();
		final JQuery menu1 = jq("@menupopup .z-menuitem-content");
		final String menu1Background = getBackground(menu1);
		final JQuery menu2 = jq(".z-menuitem-content:last");
		final String menu2Background = getBackground(menu2);
		getActions().moveToElement(toElement(menu1)).perform();
		Assertions.assertNotEquals(menu1Background, getBackground(menu1));
		getActions().moveToElement(toElement(menu2)).perform();
		Assertions.assertNotEquals(menu2Background, getBackground(menu2));
	}

	private String getBackground(JQuery w) {
		return w.css("backgroundColor");
	}
}
