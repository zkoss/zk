/* B96_ZK_4509Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Oct 28 17:21:16 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_4509Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem:contains(2)"));
		waitResponse();
		Assert.assertEquals(20, jq(".z-listitem").length());

		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem:contains(3)"));
		waitResponse();
		Assert.assertEquals(0, jq(".z-listitem").length());

		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem:contains(2)"));
		waitResponse();
		Assert.assertEquals(20, jq(".z-listitem").length());
		Assert.assertFalse(hasError());
	}
}
