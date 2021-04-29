/* BookCRUDTest.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 27 10:17:20 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class BookCRUD2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/basic/bookCrud2.zul");

		click(jq("@button:contains(Create)"));
		waitResponse();
		click(jq("@button:contains(Save)"));
		waitResponse();

		Assert.assertEquals("create shall work", "save book Name", jq(".msg-label").text());

		click(jq(".z-listitem:eq(2)"));
		waitResponse();

		Assert.assertEquals("shall update the selected listitem data in textbox", "Book 0_2", jq("@textbox:eq(0)").val());

		type(jq("@textbox:eq(0)"), "Updated Name 0_2");
		waitResponse();

		click(jq("@button:contains(Save)"));
		waitResponse();

		Assert.assertEquals("update shall work", "save book Updated Name 0_2", jq(".msg-label").text());
		Assert.assertEquals("update shall work", "Updated Name 0_2", jq(".z-listitem-selected>.z-listcell:eq(0)").text().trim());

		click(jq(".z-listitem:eq(3)"));
		waitResponse();
		String deletedName = jq("@textbox:eq(0)").val();
		click(jq("@button:contains(Delete)"));
		waitResponse();
		click(jq("@button:contains(Yes)"));
		waitResponse();
		Assert.assertFalse("Delete shall work", jq(".z-listitem-selected").exists());
		click(jq(".z-listitem:eq(3)"));
		waitResponse();
		Assert.assertNotEquals("the deleted book shall not exist on the same index", deletedName, jq("@textbox:eq(0)").val());
	}
}
