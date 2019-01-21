/* B86_ZK_4197Test.java

		Purpose:
                
		Description:
                
		History:
				Mon Jan 21 15:32:53 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4197Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-groupbox-title"));
		waitResponse();
		Assert.assertEquals("animationspeed: 0", getZKLog());
	}
}
