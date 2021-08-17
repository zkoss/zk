/* B96_ZK_4997Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 17 11:51:19 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_4997Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button:contains(with autodisable)"));
		waitResponse();
		Assert.assertTrue(jq(".z-popup-open").isVisible());
	}
}
