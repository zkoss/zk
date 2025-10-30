/* B96_ZK_4792Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar 29 12:23:54 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.Select;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4792Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Select sb = new Select(toElement(jq("@selectbox")));
		sb.selectByVisibleText("Name is data 1");
		waitResponse();
		click(jq("@button:contains(show selected index)"));
		waitResponse();
		Assertions.assertEquals("0", jq("$lb1").text());
		Assertions.assertEquals("0", jq("$lb2").text());
		Assertions.assertEquals("0", jq("$lb3").text());
		Assertions.assertEquals("0", jq("$lb4").text());
		Assertions.assertEquals("0", jq("$lb5").text());
	}
}
