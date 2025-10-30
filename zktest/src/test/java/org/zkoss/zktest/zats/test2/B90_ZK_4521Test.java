/* B90_ZK_4521Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Mar 05 16:32:02 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B90_ZK_4521Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery themesLinks = jq(".z-a");
		for (int i = 0, len = themesLinks.length(); i < len; i++) {
			click(themesLinks.eq(i));
			waitResponse();
			testCoachmark();
		}
	}
	
	private void testCoachmark() {
		JQuery jqCoachmarkContent = jq(".z-coachmark-content");
		JQuery jqCoachmarkPointer = jq(".z-coachmark-pointer");
		click(jq("@button:contains(openCoachmark)"));
		waitResponse();
		Assertions.assertEquals(600, jqCoachmarkContent.outerWidth());
		Assertions.assertEquals(100, jqCoachmarkContent.outerHeight() + jqCoachmarkPointer.outerHeight());
		
		click(jq("@button:contains(width:800)"));
		waitResponse();
		click(jq("@button:contains(height:300)"));
		waitResponse();
		Assertions.assertEquals(800, jqCoachmarkContent.outerWidth());
		Assertions.assertEquals(300, jqCoachmarkContent.outerHeight() + jqCoachmarkPointer.outerHeight());
		
		click(jq(".z-coachmark-close"));
		waitResponse();
	}
}
