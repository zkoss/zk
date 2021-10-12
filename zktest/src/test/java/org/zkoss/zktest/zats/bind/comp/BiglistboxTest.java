/* BiglistboxTest.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 26 12:55:26 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.support.ui.Select;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class BiglistboxTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertEquals("0", jq(".z-biglistbox-selected").eval("index()"));

		click(jq(".z-biglistbox-row:eq(1)"));
		waitResponse();
		final JQuery lbxObject = jq("$listbox1");
		final JQuery lbxIndex = jq("$listbox2");
		Assert.assertEquals("1", lbxObject.toElement().get("selectedIndex"));
		Assert.assertEquals("1", lbxIndex.toElement().get("selectedIndex"));

		new Select(toElement(lbxObject)).selectByIndex(2);
		waitResponse();
		Assert.assertEquals("2", jq(".z-biglistbox-selected").eval("index()"));
		Assert.assertEquals("2", lbxIndex.toElement().get("selectedIndex"));

		new Select(toElement(lbxIndex)).selectByIndex(3);
		waitResponse();
		Assert.assertEquals("3", jq(".z-biglistbox-selected").eval("index()"));
		Assert.assertEquals("3", lbxObject.toElement().get("selectedIndex"));

		click(jq("@button:contains(clear)"));
		waitResponse();
		Assert.assertEquals(0, jq(".z-biglistbox-selected").length());
		Assert.assertEquals("-1", lbxObject.toElement().get("selectedIndex"));
		Assert.assertEquals("-1", lbxIndex.toElement().get("selectedIndex"));
	}
}
