/* B95_ZK_4505Test.java
	Purpose:

	Description:

	History:
		Mon Dec 14 16:00:21 CST 2020, Created by katherinelin
Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * @author katherinelin
 */
public class B95_ZK_4505Test extends WebDriverTestCase {
	@Test
	public void test() throws InterruptedException {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		assertEquals(jq(".z-button").outerHeight(), jq(".z-groupbox-content").height());
	}
}