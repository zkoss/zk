/* B96_ZK_4870Test.java

		Purpose:
		
		Description:
		
		History:
				Wed May 26 18:33:38 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4870Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		// only test default theme here
		Assertions.assertEquals("0px", jq(".z-caption-content").css("padding"));
		Assertions.assertEquals("normal", jq(".z-caption-content").css("line-height"));
		Assertions.assertEquals("normal", jq(".z-caption-content .z-label").css("line-height"));
	}
}
