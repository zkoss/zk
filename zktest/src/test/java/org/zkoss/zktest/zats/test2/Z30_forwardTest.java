/* Z30_forwardTest.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 10:46:46 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class Z30_forwardTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		Assert.assertTrue(jq(".z-window").find(".z-button:contains(Hi)").exists());
	}
}
