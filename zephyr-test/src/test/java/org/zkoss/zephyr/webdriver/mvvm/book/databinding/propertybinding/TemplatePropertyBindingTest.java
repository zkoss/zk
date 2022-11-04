/* BasicPropertyBindingTest.java
	Purpose:

	Description:

	History:
		Fri May 07 14:24:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.propertybinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class TemplatePropertyBindingTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		//[Step 1]
		JQuery g1 = jq("$g1");
		JQuery g2 = jq("$g2");
		JQuery g1Rows = g1.find("@row");
		JQuery g2Rows = g1.find("@row");
		assertEquals(g1Rows.length(), g2Rows.length());
		for (int i = 0; i < g1Rows.length(); i++) {
			JQuery r1Labels = g1Rows.eq(i).find("@label");
			JQuery r2Labels = g2Rows.eq(i).find("@label");
			for (int j = 0; j < r1Labels.length(); j++) {
				assertEquals(r1Labels.eq(j).text(), r2Labels.eq(j).text());
			}
		}
		//[Step 2]
		JQuery g3 = jq("$g3");
		JQuery g3Rows = g3.find("@row");
		for (int i = 0; i < g3Rows.length(); i++) {
			JQuery idLabel = g3Rows.eq(i).find("@label").eq(1);
			assertEquals("*****", idLabel.text());
		}
		//[Step 3]
		click(jq("$btn1"));
		waitResponse();
		for (int i = 0; i < g3Rows.length(); i++) {
			JQuery idLabel = g3Rows.eq(i).find("@label").eq(1);
			assertNotEquals("*****", idLabel.text());
		}
		click(jq("$btn1"));
		waitResponse();
		for (int i = 0; i < g3Rows.length(); i++) {
			JQuery idLabel = g3Rows.eq(i).find("@label").eq(1);
			assertEquals("*****", idLabel.text());
		}
		//[Step 4]
		JQuery g4 = jq("$g4");
		JQuery g4Rows = g4.find("@row");
		click(jq("$btn2_1"));
		waitResponse();
		assertEquals("v", g4Rows.eq(0).find("@label").eq(4).text());
		//[Step 5]
		click(jq("$btn2_2"));
		waitResponse();
		assertEquals("", g4Rows.eq(0).find("@label").eq(4).text());
	}
}
