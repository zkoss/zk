/* B50_ZK_806Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 26 15:21:32 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_806Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button:last"));
		waitResponse();
		click(jq("@detail"));
		waitResponse();
		Assert.assertTrue(!"rgb(255, 255, 255)".equals(jq("@row:contains(Detail Content)").css("color")));
		click(jq("@button:first"));
		waitResponse();
	}
}
