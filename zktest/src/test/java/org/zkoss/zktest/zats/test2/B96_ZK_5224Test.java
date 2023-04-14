/* B96_ZK_5224Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 14 11:23:51 CST 2023, Created by jameschu

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */

public class B96_ZK_5224Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		//equal means the margin is ignored
		assertEquals(20, jq("$g1").parent().width() - jq("$g1").width(), 1);
		assertEquals(20, jq("$g2").parent().width() - jq("$g2").width(), 1);
	}
}
