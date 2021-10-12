/* B86_ZK_4123Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Dec 03 14:38:45 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4123Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$btn1"));
		waitResponse();
		Assert.assertEquals("true", getZKLog());
	}
}
