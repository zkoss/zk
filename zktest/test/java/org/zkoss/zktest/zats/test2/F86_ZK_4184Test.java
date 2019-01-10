/* F86_ZK_4184Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 08 10:40:39 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F86_ZK_4184Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		testUpdate();
		// Test 4 more times
		for (int i = 0; i < 4; i++) {
			closeAuAlert();
			testUpdate();
		}
	}

	private void testUpdate() {
		click(jq("$update"));
		waitResponse();
		Assert.assertTrue(jq("$aualert").isVisible());
	}

	private void closeAuAlert() {
		JQuery $aualert = jq("$aualert");
		if ($aualert.isVisible()) {
			click($aualert.find("@button"));
			waitResponse();
		}
	}
}
