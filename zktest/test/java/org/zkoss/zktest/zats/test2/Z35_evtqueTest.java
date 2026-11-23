/* Z35_evtqueTest.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 21 16:09:50 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class Z35_evtqueTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		type(jq("@textbox"), "AB");
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("ABABAB", jq(".z-label").eq(1).text().trim());
	}
}
