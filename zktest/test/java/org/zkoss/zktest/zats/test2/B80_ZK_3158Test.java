/** B80_ZK_3158Test.java.

 Purpose:

 Description:

 History:
 	Fri Jun 3 16:20:25 CST 2016, Created by jameschu

 Copyright (C) 2016 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 *
 */
public class B80_ZK_3158Test extends WebDriverTestCase{

    @Test
    public void test() {
		connect();
		click(jq("$btn"));
		waitResponse();
		assertEquals("B80_ZK_3158_LifeCycle: afterShadowAttached B80_ZK_3158_LifeCycle: afterShadowDetached", getZKLog());
		waitResponse();
		click(jq("$btn1"));
		waitResponse();
	}
}