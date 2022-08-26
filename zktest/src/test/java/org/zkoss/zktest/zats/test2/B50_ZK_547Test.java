/* B50_ZK_547Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 19 17:03:20 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_547Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		String expect1 = "Selected items: Item 1,";
		String expect2 = "Selected items: Item 1, Item 2,";
		String expect3 = "Selected items: Item 1, Item 2, Item 3,";
		String expect4 = "Selected items: Item 1, Item 3,";
		
		click(jq("@treerow").eq(0));
		waitResponse();
		Assertions.assertEquals(expect1, jq("@label:contains(Selected items:)").eq(0).text().trim());
		Assertions.assertEquals(expect1, jq("@label:contains(Selected items:)").eq(1).text().trim());
		act.keyDown(Keys.CONTROL).perform();
		click(jq("@treerow").eq(1));
		waitResponse();
		Assertions.assertEquals(expect2, jq("@label:contains(Selected items:)").eq(0).text().trim());
		Assertions.assertEquals(expect2, jq("@label:contains(Selected items:)").eq(1).text().trim());
		click(jq("@treecell").eq(9));
		waitResponse();
		Assertions.assertEquals(expect3, jq("@label:contains(Selected items:)").eq(0).text().trim());
		Assertions.assertEquals(expect3, jq("@label:contains(Selected items:)").eq(1).text().trim());
		click(jq("@treerow").eq(1));
		waitResponse();
		act.keyUp(Keys.CONTROL).perform();
		Assertions.assertEquals(expect4, jq("@label:contains(Selected items:)").eq(0).text().trim());
		Assertions.assertEquals(expect4, jq("@label:contains(Selected items:)").eq(1).text().trim());
		
		click(jq("@listcell").eq(0));
		waitResponse();
		Assertions.assertEquals(expect1, jq("@label:contains(Selected items:)").eq(2).text().trim());
		Assertions.assertEquals(expect1, jq("@label:contains(Selected items:)").eq(3).text().trim());
		act.keyDown(Keys.CONTROL).perform();
		click(jq("@listcell").eq(1));
		waitResponse();
		Assertions.assertEquals(expect2, jq("@label:contains(Selected items:)").eq(2).text().trim());
		Assertions.assertEquals(expect2, jq("@label:contains(Selected items:)").eq(3).text().trim());
		click(jq("@listcell").eq(2));
		waitResponse();
		Assertions.assertEquals(expect3, jq("@label:contains(Selected items:)").eq(2).text().trim());
		Assertions.assertEquals(expect3, jq("@label:contains(Selected items:)").eq(3).text().trim());
		click(jq("@listcell").eq(1));
		waitResponse();
		act.keyUp(Keys.CONTROL).perform();
		Assertions.assertEquals(expect4, jq("@label:contains(Selected items:)").eq(2).text().trim());
		Assertions.assertEquals(expect4, jq("@label:contains(Selected items:)").eq(3).text().trim());
	}
}
