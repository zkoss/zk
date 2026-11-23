/** F80_ZK_3098Test.java.

 Purpose:

 Description:

 History:
 	Mon May 30 18:14:22 CST 2016, Created by jameschu

 Copyright (C) 2016 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zktest.zats.ztl.Widget;

/**
 * @author jameschu
 *
 */
public class F80_ZK_3098Test extends WebDriverTestCase{

    @Test
    public void test() {
		connect();
		Widget widget = widget(jq("@chosenbox"));
		sendKeys(widget.$n("inp"), "i");
		waitResponse();
		JQuery jq = jq(".z-chosenbox-option");

		Element element = jq.get(0);
		click(element);
		waitResponse();
		click(jq(".z-chosenbox-item-content").get(0));
		waitResponse();
		assertEquals("Click Item: item 1", getZKLog());
	}
}