/* B85_ZK_3701Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 10 12:25:48 CST 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B85_ZK_3687Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		Assert.assertEquals("loadedScript executed afterLoad: loadedScript", getZKLog());
		closeZKLog();
		click(jq("@button").eq(0));
		waitResponse();
		Assert.assertEquals(true, getZKLog().contains("isLoaded"));
		closeZKLog();
		click(jq("@button").eq(1));
		waitResponse();
		Assert.assertEquals(false, isZKLogAvailable());
	}
}
