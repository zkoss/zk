/* B50_3169411Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 15:52:06 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_3169411Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		type(jq(".z-spinner-input:eq(0)"), "8");
		waitResponse();
		Assert.assertTrue(jq(".z-errorbox:contains(Between 2 to 6)").exists());

		type(jq(".z-spinner-input:eq(1)"), "8");
		waitResponse();
		Assert.assertTrue(jq(".z-errorbox:contains(Out of range: 2 - 6)").exists());
	}
}
