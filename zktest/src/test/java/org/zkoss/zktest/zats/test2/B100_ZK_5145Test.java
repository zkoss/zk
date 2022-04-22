/* B100_ZK_5145Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 22 09:14:41 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author katherine
 */
public class B100_ZK_5145Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("none", jq(".z-chosenbox-input").val());
	}
}
