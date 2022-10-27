/** FormProxyDirtyTest.java.

	Purpose:
		
	Description:
		
	History:
		4:19:37 PM Mar 12, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.databinding.form;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

/**
 * @author jameschu
 *
 */
public class FormProxyDirtyTest extends ClientBindTestCase {
	@Test
	public void test() {
		//		DesktopAgent desktop = connect();
		//		JQuery edit_btn = jq("$w #panel #edit_btn");
		//		edit_btn.click();
		//
		//		//add
		//		JQuery currentdata = jq("$w #panel #currentdata");
		//		JQuery categories = currentdata.getLastChild().getLastChild().getLastChild();
		//		JQuery categoryAddPanel = categories.getLastChild();
		//		JQuery categoryTextBox = categoryAddPanel.getChild(1);
		//		categoryTextBox.type("New Category");
		//		JQuery categoryAddBtn = categoryAddPanel.getLastChild();
		//		categoryAddBtn.click();
		//		JQuery categoriesList = categories.getFirstChild();
		//		assertEquals("New Category", categoriesList.getChild(3).getFirstChild().getFirstChild().getFirstChild()
		//				.as(Textbox.class).getValue());
		//		categoriesList.getChild(2).getFirstChild().getFirstChild().getLastChild().click();
		//		assertEquals("New Category", categoriesList.getChild(2).getFirstChild().getFirstChild().getFirstChild()
		//				.as(Textbox.class).getValue());
		//
		//		//cancel
		//		JQuery cancel_btn = jq("$w #panel #cancel_btn");
		//		cancel_btn.click();
		//
		//		//add again
		//		categoryTextBox.type("New Category");
		//		categoryAddBtn.click();
		//		assertEquals("New Category", categoriesList.getChild(3).getFirstChild().getFirstChild().getFirstChild()
		//				.as(Textbox.class).getValue());
		//		categoriesList.getChild(2).getFirstChild().getFirstChild().getLastChild().click();
		//		assertEquals("New Category", categoriesList.getChild(2).getFirstChild().getFirstChild().getFirstChild()
		//				.as(Textbox.class).getValue());
		//
		//		//save
		//		JQuery save_btn = jq("$w #panel #save_btn");
		//		save_btn.click();
		//
		//		//check
		//		currentdata = jq("$w #panel #currentdata");
		//		categories = currentdata.getLastChild().getLastChild().getLastChild();
		//		assertEquals("Children", categories.getChild(1).getFirstChild().text());
		//		assertEquals("Classics", categories.getChild(3).getFirstChild().text());
		//		assertEquals("New Category", categories.getChild(5).getFirstChild().text());
		//
		//		JQuery list = jq("$w #list");
		//		categories = list.getChild(1).getLastChild().getLastChild();
		//		assertEquals("Children", categories.getChild(1).getFirstChild().text());
		//		assertEquals("Classics", categories.getChild(3).getFirstChild().text());
		//		assertEquals("New Category", categories.getChild(5).getFirstChild().text());
	}
}
