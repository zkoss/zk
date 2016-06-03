/** B80_ZK_3068Test.java.

 Purpose:

 Description:

 History:
 	Tue May 31 16:20:25 CST 2016, Created by jameschu

 Copyright (C) 2016 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.openqa.selenium.Keys;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 *
 */
public class B80_ZK_3068Test extends WebDriverTestCase{

    @Test
    public void test() {
		connect();
		assertEquals(getEval("zk.currentFocus.uuid"), jq("$btn1").get(0).get("id"));
		sendKeys(jq("$btn1"), Keys.TAB);
		waitResponse();
		assertEquals(getEval("zk.currentFocus.uuid"), jq("$btn2").get(0).get("id"));
	}
}