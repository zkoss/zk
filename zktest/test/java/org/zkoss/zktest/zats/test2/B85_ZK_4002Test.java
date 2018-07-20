/* B85_ZK_4002Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Jul 20 15:27:02 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_4002Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		click(jq("$menu2"));
		waitResponse();
		Assert.assertTrue(jq("$secondmnp").exists());
	}
}
