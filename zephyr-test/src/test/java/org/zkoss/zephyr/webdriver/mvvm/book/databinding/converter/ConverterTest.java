/* ConverterTest.java
	Purpose:

	Description:

	History:
		Mon May 10 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.Keys;
import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class ConverterTest extends ZephyrClientMVVMTestCase {
	@Override
	protected boolean isHeadless() {
		return false;
	}

	@Test
	public void test() {
		connect();
		//[Step 1]
		JQuery msg1 = jq("$msg1");
		JQuery tb1_1 = jq("$tb1_1");
		JQuery tb1_2 = jq("$tb1_2");
		sendKeys(tb1_1, Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "123", Keys.TAB);
		waitResponse();
		assertTrue(jq(".z-messagebox :contains(java.text.ParseException)").exists());
		click(jq(".z-messagebox-window @button"));
		waitResponse(true);
		sendKeys(tb1_1, Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "2021/05/10", Keys.TAB);
		waitResponse();
		assertEquals("2021/05/10", msg1.text());
		assertEquals("2021/05/10", tb1_1.val());
		assertEquals("2021/05/10", tb1_2.val());
		sendKeys(tb1_2, Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "321", Keys.TAB);
		waitResponse();
		assertTrue(jq(".z-messagebox :contains(java.text.ParseException)").exists());
		click(jq(".z-messagebox-window @button"));
		waitResponse(true);
		sendKeys(tb1_2, Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "2021/05/09", Keys.TAB);
		waitResponse();
		assertEquals("2021/05/09", msg1.text());
		assertEquals("2021/05/09", tb1_1.val());
		assertEquals("2021/05/09", tb1_2.val());
		//[Step 2]
		JQuery msg2_1 = jq("$msg2_1");
		JQuery msg2_2 = jq("$msg2_2");
		JQuery msg2_3 = jq("$msg2_3");
		JQuery tb2_1 = jq("$tb2_1");
		JQuery tb2_2 = jq("$tb2_2");
		sendKeys(tb2_1, Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "1233", Keys.TAB);
		waitResponse();
		assertEquals("1233", msg2_1.text());
		assertEquals("Foo - 1233", msg2_2.text());
		assertEquals("Foo - 1233", tb2_1.val());
		assertEquals("1233 - Bar", msg2_3.text());
		assertEquals("1233 - Bar", tb2_2.val());
		sendKeys(tb2_1, Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "3211", Keys.TAB);
		waitResponse();
		assertEquals("3211", msg2_1.text());
		assertEquals("Foo - 3211", msg2_2.text());
		assertEquals("Foo - 3211", tb2_1.val());
		assertEquals("3211 - Bar", msg2_3.text());
		assertEquals("3211 - Bar", tb2_2.val());
		//[Step 3]
		JQuery msg3_1 = jq("$msg3_1");
		JQuery msg3_2 = jq("$msg3_2");
		JQuery msg3_3 = jq("$msg3_3");
		JQuery tb3_1 = jq("$tb3_1");
		JQuery tb3_2 = jq("$tb3_2");
		JQuery tb3_3 = jq("$tb3_3");
		sendKeys(tb3_1, Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "-1", Keys.TAB);
		waitResponse();
		assertEquals("-1.00", msg3_1.text());
		assertEquals("-1.00", tb3_1.val());

		sendKeys(tb3_2, Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "2021/05/01", Keys.TAB);
		waitResponse();
		assertEquals("2021/05/01", msg3_2.text());
		assertEquals("2021/05/01", tb3_2.val());

		sendKeys(tb3_3, Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE, "112233", Keys.TAB);
		waitResponse();
		assertEquals("112233", msg3_3.text());
		assertEquals("112233", tb3_3.val());
	}
}
