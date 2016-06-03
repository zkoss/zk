/** B80_ZK_2983Test.java.

 Purpose:

 Description:

 History:
 	Tue May 31 14:14:22 CST 2016, Created by jameschu

 Copyright (C) 2016 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 *
 */
public class B80_ZK_2983Test extends WebDriverTestCase{

    @Test
    public void test() {
		connect();
		click(jq("@button:eq(0)"));
		waitResponse();
		assertEquals("Value 1", jq("@textbox").val());
		assertEquals("Bob", jq(jq(".z-chosenbox-item-content").get(0)).html());
	}
}