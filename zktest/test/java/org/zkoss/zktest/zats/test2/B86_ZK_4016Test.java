/* B86_ZK_4016Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Aug 07 17:45:50 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4016Test extends WebDriverTestCase {

	@Test
	public void test() {
		B86_ZK_4016FileDealer.writeMsg("", false);
		connect();
		waitResponse();
		click(jq("$box").find("@listitem").eq(0));
		waitResponse();
		Assert.assertEquals("class org.zkoss.zktest.test2.B86_ZK_4016HMComponentonCreate\nclick\n", jq("#zk_log").toElement().get("value"));
		waitResponse();
	}
}
