/* B90_ZK_4475Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Jan 06 12:26:36 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B90_ZK_4475Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery themesLinks = jq(".z-a");
		for (int i = 0, len = themesLinks.length(); i < len; i++) {
			click(themesLinks.eq(i));
			waitResponse();
			testScrollbar();
		}
	}

	public void testScrollbar() {
		connect();
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("false", getZKLog());
		JQuery alignDiv = jq("$alignDiv");
		Assertions.assertEquals(alignDiv.find(".z-bandbox-input").positionTop(), alignDiv.find(".z-bandbox-button").positionTop());
		Assertions.assertEquals(alignDiv.find(".z-combobox-input").positionTop(), alignDiv.find(".z-combobox-button").positionTop());
		Assertions.assertEquals(alignDiv.find(".z-datebox-input").positionTop(), alignDiv.find(".z-datebox-button").positionTop());
		Assertions.assertEquals(alignDiv.find(".z-doublespinner-input").positionTop(), alignDiv.find(".z-doublespinner-button").positionTop());
		Assertions.assertEquals(alignDiv.find(".z-spinner-input").positionTop(), alignDiv.find(".z-spinner-button").positionTop());
		Assertions.assertEquals(alignDiv.find(".z-timebox-input").positionTop(), alignDiv.find(".z-timebox-button").positionTop());
	}
}
