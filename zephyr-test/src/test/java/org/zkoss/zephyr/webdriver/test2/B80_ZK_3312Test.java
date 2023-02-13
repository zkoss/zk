/* B80_ZK_3312Test.java

	Purpose:

	Description:

	History:
		Thu Jan 12 17:00:31 CST 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;


/**
 * @author jameschu
 */
public class B80_ZK_3312Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery info_origin = jq("$info_origin");
		JQuery info_selList = jq("$info_selList");
		JQuery info_selSet = jq("$info_selSet");
		JQuery btns = jq("@button");
		JQuery label_origin1 = info_origin.children().eq(1);
		JQuery label_origin2 = info_origin.children().eq(2);
		JQuery label_list1 = info_selList.children().eq(1);
		JQuery label_list2 = info_selList.children().eq(2);
		JQuery label_list3 = info_selList.children().eq(3).children().eq(1);
		JQuery label_set1 = info_selSet.children().eq(1);
		JQuery label_set2 = info_selSet.children().eq(2);
		JQuery label_set3 = info_selSet.children().eq(3).children().eq(1);
		assertEquals("[a, b, c]", label_origin1.text().trim());
		assertEquals("3", label_origin2.text().trim());
		click(btns.eq(0));
		waitResponse();
		assertEquals("[a]", label_list1.text().trim());
		assertEquals("1", label_list2.text().trim());
		assertEquals("false", label_list3.text().trim());
		assertEquals("[a]", label_set1.text().trim());
		assertEquals("1", label_set2.text().trim());
		assertEquals("false", label_set3.text().trim());
		click(btns.eq(1));
		waitResponse();
		assertEquals("[a, b]", label_list1.text().trim());
		assertEquals("2", label_list2.text().trim());
		assertEquals("false", label_list3.text().trim());
		assertEquals("[a, b]", label_set1.text().trim());
		assertEquals("2", label_set2.text().trim());
		assertEquals("false", label_set3.text().trim());
		click(btns.eq(2));
		waitResponse();
		assertEquals("[a, b, c]", label_list1.text().trim());
		assertEquals("3", label_list2.text().trim());
		assertEquals("true", label_list3.text().trim());
		assertEquals("[a, b, c]", label_set1.text().trim());
		assertEquals("3", label_set2.text().trim());
		assertEquals("false", label_set3.text().trim());

		//another test
		JQuery lb = jq("@listbox");
		assertEquals(3, lb.find("@listitem").length());
		click(btns.eq(3));
		waitResponse();
		click(btns.eq(4));
		waitResponse();
		assertEquals("[b, c]", label_origin1.text().trim());
		assertEquals("2", label_origin2.text().trim());
		assertEquals(2, lb.find("@listitem").length());
	}
}
