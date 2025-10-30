/* B96_ZK_5055Test.java

	Purpose:
		
	Description:
		
	History:
		11:44 AM 2021/11/8, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B96_ZK_5055Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery button = jq("@button");
		click(button);
		waitResponse();
		Assertions.assertEquals("OK",
				jq("$result").text());

		try {
			click(jq(".z-a:contains(Atlantic)"));
			waitResponse();
			click(button);
			waitResponse();
			Assertions.assertEquals("OK",
					jq("$result").text());
		} finally {
			click(jq(".z-a:contains(Default)"));
			waitResponse();
		}
	}
}