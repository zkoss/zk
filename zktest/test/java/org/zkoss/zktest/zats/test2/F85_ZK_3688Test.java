/* F85_ZK_3688.java

        Purpose:
                
        Description:
                
        History:
                Thu May 24 4:07 PM:59 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

public class F85_ZK_3688Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$btn1"));
		waitResponse();
		assertEquals("2001", jq("$win1").toElement().get("style.zIndex"));

		click(jq("$btn2"));
		waitResponse();
		assertEquals("5000", jq("$win2").toElement().get("style.zIndex"));

		click(jq("$btn3"));
		waitResponse();
		assertEquals("5001", jq("$pop1").toElement().get("style.zIndex"));

		click(jq("$btn4"));
		waitResponse();
		assertEquals("10000", jq("$pop2").toElement().get("style.zIndex"));

	}
}
