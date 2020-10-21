/* B95_ZK_4664Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 8 10:22:33 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;

import static org.junit.Assert.assertEquals;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B95_ZK_4664Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		JQuery input = jq(".z-paging").eq(1).find("input");
		sendKeys(input, Keys.BACK_SPACE);
		waitResponse();
		sendKeys(input, "2");
		waitResponse();
		sendKeys(input, Keys.ENTER);
		waitResponse();
		JQuery activePageLabel = jq("$currentPage");
		assertEquals("1", activePageLabel.html().trim());
		click(jq(".z-paging-previous"));
		waitResponse();
		assertEquals("0", activePageLabel.html().trim());
		click(jq(".z-paging-next"));
		waitResponse();
		assertEquals("1", activePageLabel.html().trim());
		click(jq("@button"));
		waitResponse();
		click(jq(".z-paging-button").eq(3)); //try bottom paging "Prev"
		waitResponse();
		assertEquals("0", activePageLabel.html().trim());
		click(jq(".z-paging-button").eq(5)); //try bottom paging "Next"
		waitResponse();
		assertEquals("1", activePageLabel.html().trim());
	}
}
