/* B96_ZK_4916Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 11 10:07:18 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4916Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:contains(foo)"));
		waitResponse();
		Assertions.assertEquals("foo", jq(".z-tbeditor-editor").html());

		click(jq("@button:contains(bar)"));
		waitResponse();
		Assertions.assertEquals("bar", jq(".z-tbeditor-editor").html());
	}
}
