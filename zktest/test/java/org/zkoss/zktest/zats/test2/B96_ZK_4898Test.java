/* B96_ZK_4898Test.java.

	Purpose:
		
	Description:
		
	History:
		Wed Mar 19 12:50:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.Keys;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.wcag.WcagTestOnly;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
@Category(WcagTestOnly.class)
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
