/* ForEachStatusTest.java

	Purpose:
		
	Description:
		
	History:
		Thu May 06 17:12:23 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.shadow;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class ForEachStatusTest extends ZATSTestCase {
	@Test
	public void testNumbers() {
		final DesktopAgent desktop = connect("/bind/shadow/foreach-numbers-varstatus.zul");
		final List<ComponentAgent> rows = desktop.queryAll("row");
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
		final DesktopAgent desktop = connect("/bind/shadow/foreach-items-varstatus.zul");
		final List<ComponentAgent> rows = desktop.queryAll("row");
		checkData(rows, 0, new String[]{
				"Menu[label='Home', iconSclass='z-icon-home']",
				"Menu[label='Products', iconSclass='z-icon-clone']",
				"Menu[label='Demos', iconSclass='z-icon-cube']",
				"Menu[label='Downloads', iconSclass='z-icon-download']",
				"Menu[label='Community', iconSclass='z-icon-facebook']",
				"Menu[label='About', iconSclass='z-icon-question']"
		});
		checkData(rows, 1, new String[]{"0", "1", "2", "3", "4", "5"});
		checkData(rows, 2, new String[]{"1", "2", "3", "4", "5", "6"});
		checkData(rows, 3, new String[]{"true", "false", "false", "false", "false", "false"});
		checkData(rows, 4, new String[]{"false", "false", "false", "false", "false", "true"});
		checkData(rows, 5, new String[]{"0", "0", "0", "0", "0", "0"});
		checkData(rows, 6, new String[]{"5", "5", "5", "5", "5", "5"});
		checkData(rows, 7, new String[]{"1", "1", "1", "1", "1", "1"});
	}

	private void checkData(List<ComponentAgent> rows, int columnIndex, String[] expected) {
		for (int i = 0; i < rows.size(); i++) {
			Assert.assertEquals("Error on row " + i + " col " + columnIndex,
					expected[i], getLabelValue(rows.get(i).queryAll("label").get(columnIndex)));
		}
	}

	private String getLabelValue(ComponentAgent comp) {
		return comp.as(Label.class).getValue();
	}
}
