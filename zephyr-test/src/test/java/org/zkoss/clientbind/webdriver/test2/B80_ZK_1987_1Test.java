/* B80_ZK_1987_1Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 15 16:13:47 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B80_ZK_1987_1Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery result = jq("@window $result");
		JQuery combobox = jq("@combobox");

		assertEquals(jq(".z-comboitem").get(1).get("id"), jq(".z-comboitem-selected").get(0).get("id"));

		// should fire onChange to trigger save
		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem").eq(2));
		waitResponse();
		assertEquals("Element(id=3, label=bar)", result.text());
		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem").eq(1));
		waitResponse();
		assertEquals("Element(id=2, label=foo)", result.text());
		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem").eq(0));
		waitResponse();
		assertEquals("Element(id=1, label=foo)", result.text());
		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem").eq(1));
		waitResponse();
		assertEquals("Element(id=2, label=foo)", result.text());
	}
}
