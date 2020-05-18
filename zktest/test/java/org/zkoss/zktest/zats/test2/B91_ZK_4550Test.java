/* B91_ZK_4550Test.java

		Purpose:
		
		Description:
		
		History:
				Mon May 18 17:06:19 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.ElementNotVisibleException;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B91_ZK_4550Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery orientButtons = jq("@button");
		
		for (int i = 0; i < 4; i++) { // test different orient
			click(orientButtons.eq(i));
			waitResponse();
			try {
				click(jq("$target"));
				waitResponse();
			} catch (ElementNotVisibleException e) {
				String errorMsg = "The tab2 inside nested tabbox should be visible.";
				Assert.fail(errorMsg);
			}
		}
	}
}
