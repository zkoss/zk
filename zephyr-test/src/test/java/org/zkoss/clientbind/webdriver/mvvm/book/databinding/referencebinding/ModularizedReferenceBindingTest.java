/* ModularizedReferenceBindingTest.java
	Purpose:

	Description:

	History:
		Fri May 07 18:05:11 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.databinding.referencebinding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class ModularizedReferenceBindingTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		//[Step 1]
		JQuery l1 = jq("@include $l1");
		JQuery ib1 = jq("@include $ib1");
		assertEquals("0", l1.text());
		assertEquals("0", ib1.val());
		type(ib1, "123");
		waitResponse();
		assertEquals("123", l1.text());
		assertEquals("123", ib1.val());
		//[Step 2]
		JQuery l2 = jq("@include $l2");
		JQuery tb2 = jq("@include $tb2");
		assertEquals("Dennis", l2.text());
		assertEquals("Dennis", tb2.val());
		type(tb2, "Dennis123");
		waitResponse();
		assertEquals("Dennis123", l2.text());
		assertEquals("Dennis123", tb2.val());
		JQuery l3 = jq("@include $l3");
		JQuery tb3 = jq("@include $tb3");
		assertEquals("Watson", l3.text());
		assertEquals("Watson", tb3.val());
		type(tb3, "Watson123");
		waitResponse();
		assertEquals("Watson123", l3.text());
		assertEquals("Watson123", tb3.val());
		//[Step 3]
		JQuery appendBtn = jq("@include $appendBtn");
		click(appendBtn);
		waitResponse();
		assertEquals("Dennis1231", l2.text());
		assertEquals("Dennis1231", tb2.val());
		click(appendBtn);
		waitResponse();
		assertEquals("Dennis12311", l2.text());
		assertEquals("Dennis12311", tb2.val());
		click(appendBtn);
		waitResponse();
		assertEquals("Dennis123111", l2.text());
		assertEquals("Dennis123111", tb2.val());
		//[Step 4]
		click(jq("@include $updateBtn"));
		waitResponse();
		assertEquals("Mary", l2.text());
		assertEquals("Mary", tb2.val());
		assertEquals("King", l3.text());
		assertEquals("King", tb3.val());
		//[Step 5]
		click(appendBtn);
		waitResponse();
		assertEquals("Mary1", l2.text());
		assertEquals("Mary1", tb2.val());
		click(appendBtn);
		waitResponse();
		assertEquals("Mary11", l2.text());
		assertEquals("Mary11", tb2.val());
		click(appendBtn);
		waitResponse();
		assertEquals("Mary111", l2.text());
		assertEquals("Mary111", tb2.val());
	}
}
