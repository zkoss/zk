/* B50_ZK_591Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Apr 22 11:42:56 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_591Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		click(jq("@datebox").eq(0));
		waitResponse();
		for (int i = 0; i < 4; i++) {
			act.sendKeys(Keys.TAB).perform();
			waitResponse();
			Assertions.assertEquals("This field may not be empty or contain only spaces.", jq(".z-errorbox").eq(i).text());
		}
		click(jq("@datebox").eq(0));
		waitResponse();
		act.sendKeys("Nov 11, 2011").sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertEquals(3, jq(".z-errorbox").length());
		
		click(jq("@datebox").eq(1));
		waitResponse();
		act.sendKeys("Nov 11, 2011").sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertEquals(2, jq(".z-errorbox").length());
		
		click(jq("@datebox").eq(0));
		waitResponse();
		driver.findElement(jq(".z-datebox-input").toElement()).clear();
		waitResponse();
		click(jq("@datebox").eq(0));
		waitResponse();
		act.sendKeys("Nov 10, 2011").sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertEquals(3, jq(".z-errorbox").length());
		Assertions.assertEquals("Out of range: >= Nov 11, 2011", jq(".z-errorbox").eq(2).text());
		
		driver.findElement(jq(".z-datebox-input").eq(1).toElement()).clear();
		waitResponse();
		click(jq("@datebox").eq(0));
		waitResponse();
		act.sendKeys(Keys.TAB).sendKeys("Nov 10, 2011").sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertEquals("Out of range: >= Nov 11, 2011", jq(".z-errorbox").eq(3).text());
		
		act.sendKeys("Nov 10, 2011").sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertEquals("Only date in the future or today is allowed", jq(".z-errorbox").eq(3).text());
		
		act.sendKeys("Nov 10, 2011").sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertEquals("Only today is allowed", jq(".z-errorbox").eq(3).text());
		
		act.sendKeys("Nov 10, 2011").sendKeys(Keys.TAB).perform();
		waitResponse();
		Assertions.assertEquals("Out of range: Dec 25, 2007 ~ Dec 27, 2007", jq(".z-errorbox").eq(4).text());
	}
}
