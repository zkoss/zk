/** B80_ZK_3013Test.java.

 Purpose:

 Description:

 History:
 	Tue May 31 12:14:22 CST 2016, Created by jameschu

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
public class B80_ZK_3013Test extends WebDriverTestCase{

    @Test
    public void test() {
		connect();
		selectComboitem(jq("@combobox:eq(0)").toWidget(), 1);
		waitResponse();
		selectComboitem(jq("@combobox:eq(1)").toWidget(), 1);
		waitResponse();
		assertEquals(0, jq("#zk_log").length());
	}
}