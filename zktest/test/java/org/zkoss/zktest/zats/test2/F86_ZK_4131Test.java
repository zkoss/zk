/* F86_ZK_4131Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 10:37:29 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class F86_ZK_4131Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		click(jq(".z-menu"));
		waitResponse();
		Assert.assertEquals("true", getZKLog());
	}
}
