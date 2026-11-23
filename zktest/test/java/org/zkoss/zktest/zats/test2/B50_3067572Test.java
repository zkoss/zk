/* B50_3067572Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 12:20:32 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_3067572Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-button"));
		waitResponse();

		Assert.assertTrue(jq(".z-window:contains(hello)").exists());
	}
}
