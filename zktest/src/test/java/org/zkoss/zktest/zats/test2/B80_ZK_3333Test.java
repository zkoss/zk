/* B80_ZK_3333Test.java

		Purpose:
                
		Description:
                
		History:
				Mon Mar 25 16:30:49 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B80_ZK_3333Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery editor = jq(".z-tbeditor-editor");
		sendKeys(editor, Keys.END);
		waitResponse();
		sendKeys(editor, "ss");
		waitResponse();
		click(jq(".z-button"));
		waitResponse();
		Assert.assertTrue(jq("$mid").text().contains("PROXY MSG : original messagess"));
		Assert.assertEquals("original messagess", jq("$final").text());
	}
}
