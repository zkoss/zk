/* B96_ZK_4820Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 16 12:50:44 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */

@Tag("WcagTestOnly")
public class B96_ZK_4820Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery jqChosenboxInput = jq("@chosenbox input");
		click(jqChosenboxInput);
		waitResponse();
		sendKeys(jqChosenboxInput, "b");
		waitResponse();
		assertEquals(jq(".z-chosenbox-option[aria-label=bbb4]").attr("id"), jqChosenboxInput.attr("aria-activedescendant"));
	}
}
