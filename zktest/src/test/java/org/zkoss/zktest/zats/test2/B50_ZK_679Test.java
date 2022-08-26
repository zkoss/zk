/* B50_ZK_679Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 23 16:18:46 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_679Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		process(act, false);
		driver.navigate().refresh();
		waitResponse();
		process(act, true);
	}
	
	public void process(Actions act, Boolean deleteFirstLine) {
		Assertions.assertEquals("test", jq("@textbox").eq(0).val().trim());
		click(jq("@textbox").eq(0));
		waitResponse();
		if (deleteFirstLine) {
			act.sendKeys(Keys.ARROW_LEFT).sendKeys(Keys.ARROW_LEFT).sendKeys(Keys.ARROW_LEFT).sendKeys(Keys.ARROW_LEFT).sendKeys(Keys.BACK_SPACE).perform();
		} else {
			act.sendKeys(Keys.BACK_SPACE).sendKeys(Keys.BACK_SPACE).sendKeys(Keys.BACK_SPACE).sendKeys(Keys.BACK_SPACE).perform();
		}
		click(jq("@label:contains(label 1)"));
		waitResponse();
		Assertions.assertEquals("changed times: 1", jq("@window @label:contains(changed)").text().trim());
		
		click(jq("@button"));
		waitResponse();
		System.out.println(jq("@textbox:last").val());
		Assertions.assertEquals("test", jq("@textbox").eq(1).val().trim());
		click(jq("@textbox").eq(1));
		waitResponse();
		if (deleteFirstLine) {
			act.sendKeys(Keys.ARROW_LEFT).sendKeys(Keys.ARROW_LEFT).sendKeys(Keys.ARROW_LEFT).sendKeys(Keys.ARROW_LEFT).sendKeys(Keys.BACK_SPACE).perform();
		} else {
			act.sendKeys(Keys.BACK_SPACE).sendKeys(Keys.BACK_SPACE).sendKeys(Keys.BACK_SPACE).sendKeys(Keys.BACK_SPACE).perform();
		}
		click(jq("@label:contains(label 2)"));
		waitResponse();
		Assertions.assertEquals("changed times: 2", jq("@window @label:contains(changed)").eq(1).text().trim());
	}
}
