/* B85_ZK_3942Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Jun 05 15:42:57 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3942Test extends WebDriverTestCase {
	
	@Test
	public void test() {
		connect();
		waitResponse();
		
		Assert.assertFalse(jq(".z-apply-loading").exists());
		Assert.assertEquals("onCreate\nonClientInfo", getZKLog());
	}
}
