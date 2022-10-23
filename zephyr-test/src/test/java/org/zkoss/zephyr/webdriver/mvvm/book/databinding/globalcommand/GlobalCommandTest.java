/* GlobalCommandTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.globalcommand;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class GlobalCommandTest extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		JQuery defaulGCmdBtn = jq("$defaulGCmdBtn");
		click(defaulGCmdBtn);
		waitResponse();
		String[] zkLogArr = getZKLog().split("\n");
		assertEquals("default action", zkLogArr[zkLogArr.length - 1]);

		JQuery mainArea = jq("$mainArea");
		JQuery listArea = jq("$listArea");
		click(jq("$hide"));
		waitResponse();
		assertFalse(mainArea.isVisible());
		assertFalse(listArea.isVisible());
		click(jq("$show"));
		waitResponse();
		assertTrue(mainArea.isVisible());
		assertTrue(listArea.isVisible());

		JQuery iBox = jq("$iBox");
		JQuery addBtn = jq("$addBtn");

		type(iBox, "aaa");
		waitResponse();
		click(addBtn);
		waitResponse();
		assertEquals(1, jq("$listArea @listitem").length());

		type(iBox, "bbb");
		waitResponse();
		click(addBtn);
		waitResponse();
		assertEquals(2, jq("$listArea @listitem").length());
	}
}
