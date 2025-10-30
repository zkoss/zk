/* B96_ZK_5126Test.java

	Purpose:
		
	Description:
		
	History:
		Wed May 04 12:21:48 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_5126Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals(jq("$lb").text(), jq("@chosenbox input").val());
	}
}