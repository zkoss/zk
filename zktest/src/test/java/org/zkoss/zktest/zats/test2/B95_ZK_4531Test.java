/* B95_ZK_4531Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 24 16:01:18 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B95_ZK_4531Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/test2/B95-ZK-4531.zhtml");
		click(jq("@button").eq(0));
		waitResponse();
		String $Version = getZKLog();
		closeZKLog();
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		Assert.assertNotEquals($Version, getZKLog());
	}
}
