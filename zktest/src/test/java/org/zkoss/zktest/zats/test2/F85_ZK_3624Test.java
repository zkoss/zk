/* F85_ZK_3624Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 19 12:09:37 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F85_ZK_3624Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testBy(this::increaseByButton, this::decreaseByButton);
		testBy(this::increaseByKeyboard, this::decreaseByKeyboard);
	}

	private void testBy(Runnable increase, Runnable decrease) {
		click(jq("@button:eq(0)"));
		waitResponse();

		increase.run();
		Assertions.assertEquals("1", jq(".z-spinner-input").val());
		clearSpinnerValue();

		click(jq("@button:eq(1)"));
		waitResponse();

		increase.run();
		Assertions.assertEquals("2", jq(".z-spinner-input").val());
		clearSpinnerValue();

		click(jq("@button:eq(2)"));
		waitResponse();

		decrease.run();
		Assertions.assertEquals("-1", jq(".z-spinner-input").val());
		clearSpinnerValue();

		click(jq("@button:eq(3)"));
		waitResponse();

		decrease.run();
		Assertions.assertEquals("-2", jq(".z-spinner-input").val());
		clearSpinnerValue();
	}

	private void clearSpinnerValue() {
		toElement(jq(".z-spinner-input")).clear();
	}

	private void increaseByButton() {
		click(jq(".z-spinner-up"));
		waitResponse();
	}

	private void decreaseByButton() {
		click(jq(".z-spinner-down"));
		waitResponse();
	}

	private void increaseByKeyboard() {
		click(jq(".z-spinner-input"));
		sendKeys(jq(".z-spinner-input"), Keys.UP);
		waitResponse();
	}

	private void decreaseByKeyboard() {
		click(jq(".z-spinner-input"));
		sendKeys(jq(".z-spinner-input"), Keys.DOWN);
		waitResponse();
	}
}
