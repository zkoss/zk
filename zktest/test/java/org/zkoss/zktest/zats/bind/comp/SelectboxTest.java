/* SelectboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 16:38:35 CST 2021, Created by rudyhuang

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
public class SelectboxTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		final Select select = new Select(toElement(jq("@selectbox")));
		final JQuery $selected = jq("$selected");

		select.selectByVisibleText("item04");
		waitResponse();
		Assert.assertEquals("3", $selected.text());

		select.selectByVisibleText("item02");
		waitResponse();
		Assert.assertEquals("1", $selected.text());
	}
}
