/* B100_ZK_5261Test.java

	Purpose:
		
	Description:
		
	History:
		4:58 PM 2023/5/11, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B100_ZK_5261Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		assertEquals(jq(".z-label").length(), 5);
		jq(".z-label:eq(0)").toElement().set("test", "label1"); // add extra attribute;

		// test add
		click(jq("@button:contains(add)"));
		waitResponse();
		assertEquals(jq(".z-label").length(), 6);
		assertTrue(jq(".z-label:eq(5)").text().startsWith("ddd "));
		assertEquals(jq(".z-label:eq(0)").toElement().get("test"), "label1"); // check extra attribute

		// test insertBefore
		click(jq("@button:contains(insertBefore)"));
		waitResponse();
		assertEquals(jq(".z-label").length(), 7);
		assertTrue(jq(".z-label:eq(0)").text().startsWith("ddd "));
		assertEquals(jq(".z-label:eq(1)").toElement().get("test"), "label1"); // check extra attribute

		// test remove
		click(jq("@button:contains(remove)"));
		waitResponse();
		assertEquals(jq(".z-label").length(), 6);
		assertEquals(jq(".z-label:eq(0)").toElement().get("test"), "label1"); // check extra attribute

		// test insert middle
		click(jq("@button:contains(insert middle)"));
		waitResponse();
		assertEquals(jq(".z-label").length(), 7);
		assertTrue(jq(".z-label:eq(3)").text().startsWith("ddd "));
		assertEquals(jq(".z-label:eq(0)").toElement().get("test"), "label1"); // check extra attribute

		// test
		click(jq("@button:contains(invalidate)"));
		waitResponse();
		assertEquals(jq(".z-label:eq(0)").toElement().get("test"), "null"); // check extra attribute
	}
}
