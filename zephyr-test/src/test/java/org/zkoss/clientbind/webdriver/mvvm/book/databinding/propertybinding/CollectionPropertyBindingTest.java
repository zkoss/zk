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
import org.openqa.selenium.Keys;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class CollectionPropertyBindingTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		//[Step 1]
		click(jq("$sel0Btn"));
		waitResponse();
		JQuery listbox = jq("$lb");
		JQuery selected = jq(".z-listitem-selected");
		JQuery ib_quantity = jq("$ib_quantity");
		JQuery db_price = jq("$db_price");
		assertEquals(selected.find(".z-listcell:eq(2) .z-listcell-content").text(), ib_quantity.val());
		assertEquals(Double.parseDouble(selected.find(".z-listcell:eq(3) .z-listcell-content").text()), Double.parseDouble(db_price.val()), 0.1);

		//[Step 2]
		click(jq("$sel2Btn"));
		waitResponse();
		String quantity = ib_quantity.val();
		String price = db_price.val();
		assertEquals(selected.find(".z-listcell:eq(2) .z-listcell-content").text(), quantity);
		assertEquals(Double.parseDouble(selected.find(".z-listcell:eq(3) .z-listcell-content").text()), Double.parseDouble(price), 0.1);

		//[Step 3]
		sendKeys(ib_quantity, "1");
		waitResponse();
		sendKeys(db_price, Keys.HOME, "1");
		waitResponse();
		click(jq("$saveBtn"));
		waitResponse();
		JQuery listcells = selected.find(".z-listcell");
		assertEquals(quantity + "1", listcells.eq(2).find(".z-listcell-content").text());
		assertEquals(Double.parseDouble("1" + price), Double.parseDouble(listcells.eq(3).find(".z-listcell-content").text()));
		JQuery grid = jq("$gd");
		assertEquals(quantity + "1", grid.find("@row").eq(2).find(".z-row-inner").eq(2).find("@label").text());
		assertEquals(Double.parseDouble("1" + price), Double.parseDouble(grid.find("@row").eq(2).find(".z-row-inner").eq(3).find("@label").text()));

		//[Step 4]
		click(jq("$delBtn"));
		waitResponse();
		assertEquals("", ib_quantity.val());
		assertEquals("", db_price.val());
		assertEquals(4, listbox.find("@listitem").length());
		assertEquals(4, grid.find("@row").length());

		//[Step 5]
		click(jq("$newBtn"));
		waitResponse();
		assertEquals("0", ib_quantity.val());
		assertEquals("0.00", db_price.val());
		assertEquals(5, listbox.find("@listitem").length());
		assertEquals(5, grid.find("@row").length());
	}
}
