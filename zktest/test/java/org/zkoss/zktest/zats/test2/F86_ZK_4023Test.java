/* F86_ZK_4023Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Aug 10 15:27:59 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F86_ZK_4023Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		JQuery dummyButton = jq("$dummy");
		sendKeys(jq("$d1").toWidget().$n("real"), "Aug 32, 2018");
		waitResponse();
		click(dummyButton);
		waitResponse();
		Assert.assertEquals("Sep 1, 2018", jq("$d1").toWidget().$n("real").get("value"));
		sendKeys(jq("$d2").toWidget().$n("real"), "Aug 32, 2018");
		waitResponse();
		click(dummyButton);
		waitResponse();
		Assert.assertTrue(jq(".z-errorbox").exists());
		sendKeys(jq("$d2").toWidget().$n("real"), "Feb 30, 2018");
		waitResponse();
		click(dummyButton);
		waitResponse();
		Assert.assertTrue(jq(".z-errorbox").exists());
		sendKeys(jq("$d2").toWidget().$n("real"), "Feb 0, 2018");
		waitResponse();
		click(dummyButton);
		waitResponse();
		Assert.assertTrue(jq(".z-errorbox").exists());
	}
}
