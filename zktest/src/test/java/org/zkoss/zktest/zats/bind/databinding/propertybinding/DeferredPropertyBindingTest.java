/* BasicPropertyBindingTest.java
	Purpose:

	Description:

	History:
		Fri May 07 14:24:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.propertybinding;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class DeferredPropertyBindingTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		//[Step 1]
		JQuery $tb1 = jq("$tb1");
		type($tb1, "123");
		blur($tb1);
		waitResponse();
		assertEquals("123", jq("$text1").text());
		//[Step 2]
		JQuery $tb2 = jq("$tb2");
		type($tb2, "321");
		blur($tb2);
		waitResponse();
		assertEquals("", jq("$text2").text());
		//[Step 3]
		click(jq("button"));
		waitResponse();
		assertEquals("321", jq("$text2").text());
	}
}
