/* EL3Test.java
	Purpose:

	Description:

	History:
		Tue May 04 15:42:43 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.databinding.el;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class MethodsTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		//[Step 1]
		assertEquals("my-test-valuepostfix", jq("$result1").text());
		//[Step 2]
		assertEquals("A-my-test-value", jq("$result2").text());
		//[Step 3]
		type(jq("$ib3"), "7");
		waitResponse();
		assertEquals("$07 per person", jq("$result3").text());
		//[Step 4]
		assertEquals("array v1", jq("$result4").text());
		//[Step 5]
		JQuery result5Label = jq("$result5");
		assertEquals("7", result5Label.text());
		type(jq("$ib5_1"), "10");
		waitResponse();
		assertEquals("10", result5Label.text());
		type(jq("$ib5_2"), "20");
		waitResponse();
		assertEquals("20", result5Label.text());
		//[Step 6]
		JQuery result6_1Label = jq("$result6_1");
		JQuery result6_2Label = jq("$result6_2");
		JQuery result6_3Label = jq("$result6_3");
		assertEquals(">>null", result6_1Label.text());
		assertEquals("not 123", result6_2Label.text());
		assertEquals("not 123", result6_3Label.text());
		type(jq("$tb6"), "123");
		waitResponse();
		assertEquals(">>123", result6_1Label.text());
		assertEquals("picture is 123", result6_2Label.text());
		assertEquals("picture is 123", result6_3Label.text());
		//[Step 7]
		JQuery tb12 = jq("$tb7");
		JQuery result7_1Label = jq("$result7_1");
		JQuery result7_2Label = jq("$result7_2");
		JQuery result7_3Label = jq("$result7_3");
		JQuery result7_4Label = jq("$result7_4");
		assertEquals("false", result7_1Label.text());
		assertEquals("true", result7_2Label.text());
		assertEquals("false", result7_3Label.text());
		assertEquals("true", result7_4Label.text());
		type(tb12, "16");
		waitResponse();
		assertEquals("true", result7_1Label.text());
		assertEquals("true", result7_2Label.text());
		assertEquals("false", result7_3Label.text());
		assertEquals("false", result7_4Label.text());
		type(tb12, "19");
		waitResponse();
		assertEquals("false", result7_1Label.text());
		assertEquals("false", result7_2Label.text());
		assertEquals("true", result7_3Label.text());
		assertEquals("true", result7_4Label.text());
	}
}
