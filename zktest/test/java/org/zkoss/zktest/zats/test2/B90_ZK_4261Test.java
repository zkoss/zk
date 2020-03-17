/* B90_ZK_4261Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar 16 15:03:20 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jameschu
 */
public class B90_ZK_4261Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@textbox"));
		waitResponse();
		selectAll();
		waitResponse();
		copy();
		waitResponse();
		JQuery db = jq("@decimalbox");
		click(db);
		waitResponse();
		paste();
		blur(db);
		waitResponse();
		Assert.assertEquals("12.3", db.val());
	}
}
