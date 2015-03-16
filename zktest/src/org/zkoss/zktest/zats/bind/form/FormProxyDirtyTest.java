/** FormProxyDirtyTest.java.

	Purpose:
		
	Description:
		
	History:
		4:19:37 PM Mar 12, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.form;

import static org.junit.Assert.assertEquals;


import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * @author jameschu
 *
 */
public class FormProxyDirtyTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent edit_btn = desktop.query("#w #panel #edit_btn");
		edit_btn.click();
		
		//add
		ComponentAgent currentdata = desktop.query("#w #panel #currentdata");
		ComponentAgent categories = currentdata.getLastChild().getLastChild().getLastChild();
		ComponentAgent categoryAddPanel = categories.getLastChild();
		ComponentAgent categoryTextBox = categoryAddPanel.getChild(1);
		categoryTextBox.type("New Category");
		ComponentAgent categoryAddBtn = categoryAddPanel.getLastChild();
		categoryAddBtn.click();
		ComponentAgent categoriesList = categories.getFirstChild();
		assertEquals("New Category", categoriesList.getChild(3).getFirstChild().getFirstChild().getFirstChild().as(Textbox.class).getValue());
		categoriesList.getChild(2).getFirstChild().getFirstChild().getLastChild().click();
		assertEquals("New Category", categoriesList.getChild(2).getFirstChild().getFirstChild().getFirstChild().as(Textbox.class).getValue());
		
		//cancel
		ComponentAgent cancel_btn = desktop.query("#w #panel #cancel_btn");
		cancel_btn.click();
		
		//add again
		categoryTextBox.type("New Category");
		categoryAddBtn.click();
		assertEquals("New Category", categoriesList.getChild(3).getFirstChild().getFirstChild().getFirstChild().as(Textbox.class).getValue());
		categoriesList.getChild(2).getFirstChild().getFirstChild().getLastChild().click();
		assertEquals("New Category", categoriesList.getChild(2).getFirstChild().getFirstChild().getFirstChild().as(Textbox.class).getValue());
		
		//save
		ComponentAgent save_btn = desktop.query("#w #panel #save_btn");
		save_btn.click();
		
		//check
		currentdata = desktop.query("#w #panel #currentdata");
		categories = currentdata.getLastChild().getLastChild().getLastChild();
		assertEquals("Children", categories.getChild(1).getFirstChild().as(Label.class).getValue());
		assertEquals("Classics", categories.getChild(3).getFirstChild().as(Label.class).getValue());
		assertEquals("New Category", categories.getChild(5).getFirstChild().as(Label.class).getValue());
		
		ComponentAgent list = desktop.query("#w #list");
		categories = list.getChild(1).getLastChild().getLastChild();
		assertEquals("Children", categories.getChild(1).getFirstChild().as(Label.class).getValue());
		assertEquals("Classics", categories.getChild(3).getFirstChild().as(Label.class).getValue());
		assertEquals("New Category", categories.getChild(5).getFirstChild().as(Label.class).getValue());
	}
}
