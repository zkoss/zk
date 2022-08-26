/* B96_ZK_4824Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Mar 26 12:01:39 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

@Tag("WcagTestOnly")
public class B96_ZK_4824Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals("pass", getZKLog(), "The aria-owns of radio group should only contains external radio's id.");
	}
}