/* B85_ZK_3932.java

        Purpose:
                
        Description:
                
        History:
                Thu Jun 28 10:46:51 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3932Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		click(jq(".z-button:eq(0)"));
		waitResponse();
		Assert.assertFalse(jq(".z-error").exists());
		
		click(jq(".z-button:eq(1)"));
		waitResponse();
		Assert.assertFalse(jq(".z-error").exists());
	}
}
