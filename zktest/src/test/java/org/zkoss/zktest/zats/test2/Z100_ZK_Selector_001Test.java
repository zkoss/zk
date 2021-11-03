/* Z100_ZK_Selector_001Test.java

	Purpose:
		
	Description:
		
	History:
		11:50 AM 2021/11/3, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class Z100_ZK_Selector_001Test  extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		click(jq("@button:contains(Test Button)"));
		waitResponse(true);

		Assert.assertEquals("OK", jq(".z-label").eq(1).text().trim());
	}
}
