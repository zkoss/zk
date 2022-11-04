/** FormProxyDirtyTest.java.

	Purpose:
		
	Description:
		
	History:
		4:19:37 PM Mar 12, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.form;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class FormProxyDirtyTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery edit_btn = jq("$w $panel $edit_btn");
		click(edit_btn);
		waitResponse();
		//add
		JQuery currentdata = jq("$w $panel $currentdata");
		JQuery categories = currentdata.find("@row").eq(2);
		JQuery categoryAddPanel = categories.find("@hlayout").last();
		JQuery categoryTextBox = categoryAddPanel.find("@textbox");
		type(categoryTextBox, "New Category");
		waitResponse();
		JQuery categoryAddBtn = categoryAddPanel.find("@a");
		click(categoryAddBtn);
		waitResponse();
		assertEquals("New Category", categories.find("@textbox").eq(3).toWidget().get("value"));
		click(categories.find("@listbox @hlayout").eq(2).find("@a"));
		waitResponse();
		assertEquals("New Category", categories.find("@textbox").eq(2).toWidget().get("value"));
		//cancel
		JQuery cancel_btn = jq("$w $panel $cancel_btn");
		click(cancel_btn);
		waitResponse();
		//add again
		type(categoryTextBox, "New Category");
		waitResponse();
		click(categoryAddBtn);
		waitResponse();
		assertEquals("New Category", categories.find("@textbox").eq(3).toWidget().get("value"));
		click(categories.find("@listbox @hlayout").eq(2).find("@a"));
		waitResponse();
		assertEquals("New Category", categories.find("@textbox").eq(2).toWidget().get("value"));

		//save
		JQuery save_btn = jq("$w $panel $save_btn");
		click(save_btn);
		waitResponse();
		//check
		assertEquals("Children", categories.find(".z-row-inner").eq(1).find("@label").eq(0).text());
		assertEquals("Classics", categories.find(".z-row-inner").eq(1).find("@label").eq(1).text());
		assertEquals("New Category", categories.find(".z-row-inner").eq(1).find("@label").eq(2).text());

		JQuery list = jq("$w $list");
		categories = list.find("@listitem").eq(0).find(".z-listcell").eq(2);
		assertEquals("Children", categories.find("@label").eq(0).text());
		assertEquals("Classics", categories.find("@label").eq(1).text());
		assertEquals("New Category", categories.find("@label").eq(2).text());
	}
}
