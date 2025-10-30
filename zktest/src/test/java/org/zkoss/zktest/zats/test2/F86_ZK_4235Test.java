/* F86_ZK_4235Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 10 17:09:02 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F86_ZK_4235Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		WebElement db1 = driver.findElement(By.cssSelector(".db1 .z-datebox-input"));
		WebElement db2 = driver.findElement(By.cssSelector(".db2 .z-datebox-input"));
		
		changeInputValue(db1, db2, "01-01-29" , "01-01-28");
		checkYear("1929", "2028");
		
		click(jq("@button").eq(0));
		waitResponse();
		changeInputValue(db1, db2, "01-02-29" , "01-02-28");
		checkYear("29", "128");
		
		click(jq("@button").eq(1));
		waitResponse();
		changeInputValue(db1, db2, "01-01-29" , "01-01-28");
		checkYear("529", "528");
		
		click(jq("@button").eq(2));
		waitResponse();
		changeInputValue(db1, db2, "01-02-29" , "01-02-28");
		checkYear("1529", "1528");
		
		click(jq("@button").eq(3));
		waitResponse();
		changeInputValue(db1, db2, "01-01-29" , "01-01-28");
		checkYear("2029", "2028");
		
		click(jq("@button").eq(4));
		waitResponse();
		changeInputValue(db1, db2, "01-02-29" , "01-02-28");
		checkYear("200029", "200028");
	}
	
	private void checkYear(String expect1, String expect2) {
		click(jq(".z-datebox-button").eq(0));
		waitResponse();
		Assertions.assertEquals(expect1, jq(".z-calendar-text:last").text().trim());
		click(jq(".z-datebox-button").eq(1));
		waitResponse();
		Assertions.assertEquals(expect2, jq(".z-calendar-text:last").text().trim());
	}
	
	private void changeInputValue(WebElement db1, WebElement db2, String input1, String input2) {
		db1.clear();
		waitResponse();
		db1.sendKeys(input1);
		waitResponse();
		db2.clear();
		waitResponse();
		db2.sendKeys(input2);
		waitResponse();
		click(jq(".z-label"));
		waitResponse();
	}
}
