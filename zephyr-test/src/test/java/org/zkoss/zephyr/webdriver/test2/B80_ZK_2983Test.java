/** B80_ZK_2983Test.java.

 Purpose:

 Description:

 History:
 	Tue May 31 14:14:22 CST 2016, Created by jameschu

 Copyright (C) 2016 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 *
 */
@Disabled
public class B80_ZK_2983Test extends WebDriverTestCase {

    @Test
    public void test() {
		connect();
		click(jq("@button:eq(0)"));
		waitResponse();
		assertEquals("Value 1", jq("@textbox").val());
		assertEquals("Bob", jq(jq(".z-chosenbox-item-content").get(0)).html());
	}
}