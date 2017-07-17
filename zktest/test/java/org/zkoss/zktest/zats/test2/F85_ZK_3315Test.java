/* F85_ZK_3315Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 17 09:34:19 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F85_ZK_3315Test extends WebDriverTestCase {
	@Test
	public void testOverrideAuOnResp() throws Exception {
		connect();
		String typeString = "Hello World";
		type(jq("@textbox:eq(0)"), typeString);
		waitResponse();

		Assert.assertEquals(typeString, jq("$lbl").text());
		String log = getZKLog();
		Assert.assertTrue(log.contains("[Au] injected.") && log.contains("[Au] success. SID = "));
	}
}
