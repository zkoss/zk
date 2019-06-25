/* B86_ZK_4292Test.java

		Purpose:
		
		Description:
		
		History:
				Wed May 15 12:43:02 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4292Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button:first"));
		waitResponse();
		Assert.assertEquals("100", getZKLog());
		click(jq("@button:last"));
		waitResponse();
		Assert.assertEquals("100\n99", getZKLog());
	}
}
