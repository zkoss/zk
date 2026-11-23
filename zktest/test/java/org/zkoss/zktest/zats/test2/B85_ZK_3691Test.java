/* B85_ZK_3691Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Apr 09 10:36:51 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3691Test extends WebDriverTestCase {
	
	@Test
	public void test() {
		connect();
		
		click(jq(".z-listheader-checkable"));
		waitResponse(true);
		
		for (String log : getZKLog().split("\n")) {
			Assert.assertEquals("org.zkoss.zul.Listitem", log);
		}
	}
}
