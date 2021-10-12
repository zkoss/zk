/* B95_ZK_4517_AUTest.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 08 12:50:34 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4517_AUTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		Assert.assertNotEquals(0, jq("@listitem").length());
	}
}
