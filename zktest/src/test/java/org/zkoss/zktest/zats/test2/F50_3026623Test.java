/* F50_3026623Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 09 10:54:15 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F50_3026623Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		waitResponse();
		type(jq("@textbox:first"), "111");
		waitResponse();
		type(jq("@textbox:first"), "222");
		waitResponse();

		// main
		Assertions.assertEquals("111\n222\n", jq("textarea").val());
		// frame2
		Assertions.assertEquals("111\n222\n", jq("iframe").contents().find("textarea").val());
		// frame1 frame2
		Iterable<JQuery> textareas = jq("iframe").contents().find("iframe").contents().find("textarea");
		for (JQuery t : textareas) {
			Assertions.assertEquals("111\n222\n", t.val());
		}
	}
}
