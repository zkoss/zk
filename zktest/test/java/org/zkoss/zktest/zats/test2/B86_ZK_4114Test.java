/* B86_ZK_4114Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Nov 02 19:04:23 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4114Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		Assert.assertEquals(jq(".z-orgnode:contains(Item1)").css("fontFamily"),
				jq(".z-label:contains(Item2)").css("fontFamily"));
	}
}
