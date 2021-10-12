/* B95_ZK_4714Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 1 12:47:36 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B95_ZK_4714Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse(20000);
		assertNoJSError();
		JQuery test04 = jq("@test04");
		Assert.assertEquals("700", test04.css("font-weight"));
		Assert.assertEquals("rgb(255, 0, 0)", test04.css("color"));
	}
}
