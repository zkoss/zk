/* B96_ZK_5050Test.java

	Purpose:
		
	Description:
		
	History:
		12:28 PM 2021/11/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5050Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals(5, jq(".z-treerow").length());

		click(jq("@button"));
		waitResponse();
		assertEquals(5, jq(".z-treerow").length());
	}
}
