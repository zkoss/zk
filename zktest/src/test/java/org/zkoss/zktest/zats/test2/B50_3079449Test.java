/* B50_3079449Test.java

		Purpose:
                
		Description:
                
		History:
				Thu Mar 21 12:25:43 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_3079449Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertTrue(jq("span:contains(If you see this page, it is correct)").exists());
	}
}
