/* B95_ZK_4702Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 27 18:10:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4702Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals(jq(".z-tree-body").height(), jq(".z-treerow").height() * 5, 5); //because times 5
	}
}
