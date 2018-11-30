/* B86_ZK_3940Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Nov 20 14:58:30 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_3940Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		click(jq("@button:contains(7500)"));
		waitResponse();
		Assert.assertEquals("true", getZKLog());
		closeZKLog();
		
		click(jq("@button:contains(8100)"));
		waitResponse();
		Assert.assertEquals("true", getZKLog());
		closeZKLog();
		
		click(jq("@button:contains(65000)"));
		waitResponse();
		Assert.assertEquals("true", getZKLog());
		closeZKLog();
		
		click(jq("@button:contains(66000)"));
		waitResponse();
		Assert.assertEquals("true", getZKLog());
		closeZKLog();
		
		click(jq("@button:contains(rare words)"));
		waitResponse();
		Assert.assertEquals("true", getZKLog());
	}
}
