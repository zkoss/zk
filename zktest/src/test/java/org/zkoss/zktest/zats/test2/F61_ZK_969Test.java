/* F61_ZK_969Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 16 16:59:07 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F61_ZK_969Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		selectComboitem(widget("@combobox"), 0);
		Assertions.assertEquals("item 1", jq("@combobox input").val());

		int westWidth = jq(".z-west-header").width();
		getActions().dragAndDropBy(toElement(jq(".z-west-splitter:eq(0)")), -100, 0)
				.perform();
		waitResponse();
		assertThat(jq(".z-west-header").width(), lessThan(westWidth));

		click(widget("@north").$n("btn"));
		waitResponse(true);
		Assertions.assertTrue(jq(".z-north-collapsed").isVisible());
	}
}
