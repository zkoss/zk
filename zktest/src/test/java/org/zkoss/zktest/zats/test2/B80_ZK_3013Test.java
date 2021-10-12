/** B80_ZK_3013Test.java.

 Purpose:

 Description:

 History:
 	Tue May 31 12:14:22 CST 2016, Created by jameschu

 Copyright (C) 2016 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

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