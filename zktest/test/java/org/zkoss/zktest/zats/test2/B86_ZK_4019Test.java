/* B86_ZK_4019Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Aug 17 11:57:31 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4019Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		click(jq("@button"));
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assert.assertTrue("jq(\".z-columns-bar\") is visible",jq(".z-columns-bar").width() > 0);
	}
}
