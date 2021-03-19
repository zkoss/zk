/* B96_ZK_4600Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 16 16:16:56 CST 2021, Created by katherinelin

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B96_ZK_4600Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery spinner = jq(".z-spinner");
		JQuery doubleSpinner = jq(".z-doublespinner");
		click(spinner.find(".z-spinner-up"));
		click(doubleSpinner.find(".z-doublespinner-up"));
		click(jq(".z-datebox-button"));
		waitResponse();
		click(jq(".z-calendar-cell"));
		click(jq(".z-datebox-button"));
		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem"));
		waitResponse();
		Assert.assertEquals("0", spinner.find(".z-spinner-input").val());
		Assert.assertEquals("0.0", doubleSpinner.find(".z-doublespinner-input").val());
		Assert.assertEquals("12/31/2000", jq(".z-datebox-input").val());
		Assert.assertEquals("0", jq(".z-combobox-input").val());
	}
}
