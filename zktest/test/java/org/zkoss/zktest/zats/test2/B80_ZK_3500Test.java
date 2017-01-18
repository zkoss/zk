/* B80_ZK_3500Test.java

	Purpose:

	Description:

	History:
		Wed Jan 11 14:14:32 CST 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.openqa.selenium.Keys;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author jameschu
 */
public class B80_ZK_3500Test extends WebDriverTestCase{
    @Test
    public void test() {
        connect();
		JQuery ch = jq("@chosenbox");
		JQuery inp = ch.find("input");
		type(inp, "a");
		waitResponse();
		sendKeys(ch, Keys.ENTER);
		waitResponse();
		assertEquals(1, jq(".z-chosenbox-item").length());
		type(inp, "aa");
		waitResponse();
		sendKeys(ch, Keys.ENTER);
		waitResponse();
		assertEquals(2, jq(".z-chosenbox-item").length());
    }
}
