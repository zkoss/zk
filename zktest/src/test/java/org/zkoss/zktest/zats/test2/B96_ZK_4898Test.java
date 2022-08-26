/* B96_ZK_4898Test.java.

	Purpose:
		
	Description:
		
	History:
		Wed Mar 19 12:50:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
@Tag("WcagTestOnly")
public class B96_ZK_4898Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		List<String> inputSelectors = Arrays.asList("$input1", "$input2 input", "$input3 input", "$input4 input", "$input5 input");
		List<Keys> testKeys =  Arrays.asList(Keys.ARROW_UP, Keys.ARROW_DOWN, Keys.ARROW_LEFT, Keys.ARROW_RIGHT);
		for (String selector : inputSelectors) {
			JQuery $input = jq(selector);
			for (Keys key: testKeys) {
				click($input);
				waitResponse();
				assertTrue($input.is(":focus"));
				getActions().sendKeys(key).perform();
				waitResponse();
				assertTrue($input.is(":focus"));
			}
		}
	}
}
