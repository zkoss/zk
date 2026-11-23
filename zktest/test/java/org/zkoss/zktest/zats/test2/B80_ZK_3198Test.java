/** B80_ZK_3198Test.java.

 Purpose:

 Description:

 History:
 	Tue Jun 7 17:54:25 CST 2016, Created by jameschu

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
public class B80_ZK_3198Test extends WebDriverTestCase{

    @Test
    public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertEquals("1", getZKLog());
	}
}