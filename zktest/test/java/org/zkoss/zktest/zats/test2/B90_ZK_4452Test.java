/* B90_ZK_4452Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Mar 3 14:26:36 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.lang.Strings;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B90_ZK_4452Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery themesLinks = jq(".z-a");
		for (int i = 0, len = themesLinks.length(); i < len; i++) {
			click(themesLinks.eq(i));
			waitResponse();
			testErrorBtnStyle();
		}
	}
	
	private void testErrorBtnStyle() {
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals(jq(".z-bandbox-invalid").css("border"), jq(".z-bandbox-button").css("border-left"));
		Assert.assertEquals(jq(".z-combobox-invalid").css("border"), jq(".z-combobox-button").css("border-left"));
		Assert.assertEquals(jq(".z-datebox-invalid").css("border"), jq(".z-datebox-button").css("border-left"));

		JQuery alignDiv = jq("$alignDiv");
		Assert.assertEquals(alignDiv.find(".z-bandbox-input").positionTop(), alignDiv.find(".z-bandbox-button").positionTop());
		Assert.assertEquals(alignDiv.find(".z-combobox-input").positionTop(), alignDiv.find(".z-combobox-button").positionTop());
		Assert.assertEquals(alignDiv.find(".z-datebox-input").positionTop(), alignDiv.find(".z-datebox-button").positionTop());
		Assert.assertEquals(alignDiv.find(".z-doublespinner-input").positionTop(), alignDiv.find(".z-doublespinner-button").positionTop());
		Assert.assertEquals(alignDiv.find(".z-spinner-input").positionTop(), alignDiv.find(".z-spinner-button").positionTop());
		Assert.assertEquals(alignDiv.find(".z-timebox-input").positionTop(), alignDiv.find(".z-timebox-button").positionTop());
	}
}
