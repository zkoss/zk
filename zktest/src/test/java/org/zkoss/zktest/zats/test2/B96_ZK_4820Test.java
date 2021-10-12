/* B96_ZK_4820Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 16 12:50:44 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.wcag.WcagTestOnly;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */

@Category(WcagTestOnly.class)
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
