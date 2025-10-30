/* B96_ZK_5153Test.java

	Purpose:
		
	Description:
		
	History:
		3:46 PM 2022/10/4, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5153Test extends WebDriverTestCase {
	@Test
	public void testFlex() {
		connect();
		int width = jq("@vlayout").height();
		click(jq("@button"));
		waitResponse();
		assertEquals(width, jq("@vlayout").height(), 1);
	}
}
