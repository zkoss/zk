/* B85_ZK_3829Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 11:34:35 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B85_ZK_3829Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assertions.assertEquals(1, jq(".z-label[style*=\"color: red\"]:contains(fails2)").length());
		Assertions.assertEquals(2, jq(".z-label[style*=\"color: red\"]:contains(fails)").length());
	}
}
