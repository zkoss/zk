/* ForEachStatusTest.java

	Purpose:
		
	Description:
		
	History:
		Thu May 06 17:12:23 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.shadow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class ForEachStatusTest extends ClientBindTestCase {
	@Test
	public void testNumbers() {
		connect("/mvvm/book/shadow/iterate/foreach-numbers-varstatus.zul");
		JQuery rows = jq("@row");
		checkData(rows, 0, new String[]{"4", "7", "10", "13", "16", "19"});
		checkData(rows, 1, new String[]{"4", "7", "10", "13", "16", "19"});
		checkData(rows, 2, new String[]{"5", "8", "11", "14", "17", "20"});
		checkData(rows, 3, new String[]{"true", "false", "false", "false", "false", "false"});
		checkData(rows, 4, new String[]{"false", "false", "false", "false", "false", "true"});
		checkData(rows, 5, new String[]{"4", "4", "4", "4", "4", "4"});
		checkData(rows, 6, new String[]{"20", "20", "20", "20", "20", "20"});
		checkData(rows, 7, new String[]{"3", "3", "3", "3", "3", "3"});
	}

	@Test
	public void testItems() {
		connect("/mvvm/book/shadow/iterate/foreach-items-varstatus.zul");
		JQuery rows = jq("@row");
		checkData(rows, 0, new String[]{"Menu[label='Home', iconSclass='z-icon-home']", "Menu[label='Products', iconSclass='z-icon-clone']", "Menu[label='Demos', iconSclass='z-icon-cube']", "Menu[label='Downloads', iconSclass='z-icon-download']", "Menu[label='Community', iconSclass='z-icon-facebook']", "Menu[label='About', iconSclass='z-icon-question']"});
		checkData(rows, 1, new String[]{"0", "1", "2", "3", "4", "5"});
		checkData(rows, 2, new String[]{"1", "2", "3", "4", "5", "6"});
		checkData(rows, 3, new String[]{"true", "false", "false", "false", "false", "false"});
		checkData(rows, 4, new String[]{"false", "false", "false", "false", "false", "true"});
		checkData(rows, 5, new String[]{"0", "0", "0", "0", "0", "0"});
		checkData(rows, 6, new String[]{"5", "5", "5", "5", "5", "5"});
		checkData(rows, 7, new String[]{"1", "1", "1", "1", "1", "1"});
	}

	private void checkData(JQuery rows, int columnIndex, String[] expected) {
		for (int i = 0; i < rows.length(); i++) {
			assertEquals(expected[i], getLabelValue(rows.eq(i).find("@label").eq(columnIndex)), "Error on row " + i + " col " + columnIndex);
		}
	}

	private String getLabelValue(JQuery comp) {
		return comp.text();
	}
}
