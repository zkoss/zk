/* BasicPropertyBindingTest.java
	Purpose:

	Description:

	History:
		Thu May 06 18:24:49 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.databinding.propertybinding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class ConditionalPropertyBindingTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		//[Step 1]
		JQuery selected = jq(".z-listitem-selected");

		JQuery ib_quantity = jq("$ib_quantity");
		JQuery db_price = jq("$db_price");
		assertEquals(selected.find(".z-listcell:eq(1) .z-listcell-content").text(), ib_quantity.val());
		double selectedPrice = Double.parseDouble(selected.find(".z-listcell:eq(2) .z-listcell-content").text());
		assertEquals(selectedPrice, Double.parseDouble(db_price.val()), 0.1);

		JQuery p1 = jq("$p1");
		JQuery p2_1 = jq("$p2_1");
		JQuery p2_2 = jq("$p2_2");
		String initPrice = p1.text();
		assertEquals(String.valueOf(selectedPrice), initPrice);
		assertEquals(String.valueOf(selectedPrice), p2_1.text());
		assertEquals("-1.0", p2_2.text());
		JQuery p3 = jq("$p3");
		JQuery p4 = jq("$p4");
		JQuery p5 = jq("$p5");
		JQuery p6 = jq("$p6");
		assertEquals("", p3.text());
		assertEquals("", p4.text());
		assertEquals("", p5.text());
		assertEquals("", p6.text());

		//[Step 2]
		click(jq("$sel2Btn"));
		waitResponse();
		assertEquals(selected.find(".z-listcell:eq(1) .z-listcell-content").text(), ib_quantity.val());
		selectedPrice = Double.parseDouble(selected.find(".z-listcell:eq(2) .z-listcell-content").text());
		assertEquals(selectedPrice, Double.parseDouble(db_price.val()), 0.1);
		assertEquals(initPrice, p1.text());
		assertEquals(String.valueOf(selectedPrice), p2_1.text());
		assertEquals("-1.0", p2_2.text());
		assertEquals("", p3.text());
		assertEquals("", p4.text());
		assertEquals("", p5.text());
		assertEquals("", p6.text());

		//[Step 3]
		String lastPrice = p2_1.text();
		type(ib_quantity, "123");
		waitResponse();
		type(db_price, "321");
		waitResponse();
		click(jq("$saveBtn"));
		waitResponse();
		assertEquals(initPrice, p1.text());
		selectedPrice = Double.parseDouble(selected.find(".z-listcell:eq(2) .z-listcell-content").text());
		assertEquals(String.valueOf(selectedPrice), p2_1.text());
		assertEquals(lastPrice, p2_2.text());
		assertEquals("321.0", p3.text());
		assertEquals("", p4.text());
		assertEquals("321.0", p5.text());
		assertEquals("321.0", p6.text());

		//[Step 4]
		type(jq("$tb1"), "222");
		type(jq("$tb4"), "111");
		click(jq("$delBtn"));
		waitResponse();
		assertEquals("", ib_quantity.val());
		assertEquals("", db_price.val());
		assertEquals(initPrice, p1.text());
		assertEquals("", p2_1.text());
		assertEquals("222.0", p2_2.text());
		assertEquals("321.0", p3.text());
		assertEquals("321.0", p4.text());
		assertEquals("", p5.text());
		assertEquals("222.0", p6.text());

		//[Step 5]
		click(jq("$sel0Btn"));
		waitResponse();
		type(jq("$tb3"), "333");
		click(jq("$newBtn"));
		waitResponse();
		assertEquals("0", ib_quantity.val());
		assertEquals("0.00", db_price.val());
		assertEquals(initPrice, p1.text());
		assertEquals("0.0", p2_1.text());
		assertEquals("333.0", p2_2.text());
		assertEquals("321.0", p3.text());
		assertEquals("321.0", p4.text());
		assertEquals("", p5.text());
		assertEquals("222.0", p6.text());
	}
}
