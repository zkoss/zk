/* B95_ZK_4694Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 07 15:14:15 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4694Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse(); // Test fails if processing hangs
		Assert.assertEquals("onClientInfonull", getZKLog());
	}
}
