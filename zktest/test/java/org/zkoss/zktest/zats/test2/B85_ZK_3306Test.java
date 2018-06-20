/* B85_ZK_3306Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jun 20 15:06:41 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class B85_ZK_3306Test extends WebDriverTestCase {
	@Test
	public void test () {
		connect();
		click(jq("@button"));
		waitResponse();
		JQuery jmhome = jq("$mhome");
		assertEquals("rgb(255, 0, 0)",jmhome.find("span").css("color"));
		assertEquals("rgb(0, 128, 0)",jmhome.find("a").css("background-color"));
		assertEquals("0px",jmhome.css("margin"));
		assertFalse(jmhome.find("span").toElement().eval("style").matches("(.*)width(.*)"));
	}
}
