/* B85_ZK_3895Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 11:25:36 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3895Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertEquals("true", jq(".z-label").eq(10).text());
		Assert.assertEquals("true", jq(".z-label").eq(13).text());
		Assert.assertEquals("Tabbox1 is right: true\nTabbox2 is left: true", getZKLog());
	}
}
