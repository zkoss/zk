/* B50_3057902Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 12:08:41 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_3057902Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-button:eq(0)"));
		waitResponse();

		click(jq(".z-button:eq(1)"));
		waitResponse();

		Assert.assertTrue(jq(".z-apply-loading:contains(show the msg)").exists());
	}
}
