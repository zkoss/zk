/* B86_ZK_4151Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Dec 13 15:36:10 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4151Test extends WebDriverTestCase {
	@Test
	public void test() {
		driver = connect();
		Actions act = new Actions(driver);
		WebElement row = toElement(jq("$row"));
		
		act.moveToElement(row, 10, 10).clickAndHold().moveByOffset(20, 20).build().perform();
		String dragmsg1 = jq(".z-drop-content").text();
		act.release().build().perform();
		
		click(jq("$detail"));
		waitResponse();
		
		act.moveToElement(row, 10, 10).clickAndHold().moveByOffset(20, 20).build().perform();
		String dragmsg2 = jq(".z-drop-content").text();
		
		Assertions.assertTrue(dragmsg1.equals(dragmsg2));
	}
}
