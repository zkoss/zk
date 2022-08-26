/* F90_ZK_4378Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Sep 24 17:28:07 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.zul.Checkbox;

public class F90_ZK_4378Test extends WebDriverTestCase {
	private final static String CHECKED = Checkbox.State.CHECKED.toString();
	private final static String UNCHECKED = Checkbox.State.UNCHECKED.toString();
	private final static String INDETERMINATE = Checkbox.State.INDETERMINATE.toString();
	
	@Test
	public void test() {
		connect();
		waitResponse();
		Element checkboxInp = widget("@checkbox").$n("real");
		JQuery stateCheckButton = jq("@button:contains(stateCheck)");
		
		clickButtonAndCheckLog(stateCheckButton, UNCHECKED);
		
		click(checkboxInp);
		waitResponse();
		clickButtonAndCheckLog(stateCheckButton, INDETERMINATE);
		
		click(checkboxInp);
		waitResponse();
		clickButtonAndCheckLog(stateCheckButton, CHECKED);
		
		click(checkboxInp);
		waitResponse();
		clickButtonAndCheckLog(stateCheckButton, UNCHECKED);
		
		sendKeys(checkboxInp, Keys.SPACE);
		waitResponse();
		clickButtonAndCheckLog(stateCheckButton, INDETERMINATE);
		
		sendKeys(checkboxInp, Keys.SPACE);
		waitResponse();
		clickButtonAndCheckLog(stateCheckButton, CHECKED);
		
		sendKeys(checkboxInp, Keys.SPACE);
		waitResponse();
		clickButtonAndCheckLog(stateCheckButton, UNCHECKED);
		
		click(jq("@button:contains(setDisabled(true))"));
		waitResponse();
		click(checkboxInp);
		waitResponse();
		clickButtonAndCheckLog(stateCheckButton, UNCHECKED);
	}
	
	private void clickButtonAndCheckLog(JQuery button, String expected) {
		click(button);
		waitResponse();
		Assertions.assertEquals(expected, getZKLog());
		closeZKLog();
	}
}
