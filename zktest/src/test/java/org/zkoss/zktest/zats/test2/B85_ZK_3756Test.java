/* B85_ZK_3756Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Mar 07 4:20 PM:08 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B85_ZK_3756Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		click(jq("@button"));
		waitResponse();
		JQuery inc1 = jq(".inc .z-label");
		JQuery inc2 = jq(".inc2 .z-label");
		Assert.assertEquals("included", inc1.text().trim());
		Assert.assertEquals("included", inc2.text().trim());

	}

}
