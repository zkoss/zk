/** B80_ZK_3198Test.java.

 Purpose:

 Description:

 History:
 	Tue Jun 7 17:54:25 CST 2016, Created by jameschu

 Copyright (C) 2016 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

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