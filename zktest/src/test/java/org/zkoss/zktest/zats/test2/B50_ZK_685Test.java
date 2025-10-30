/* B50_ZK_685Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 23 17:06:36 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_685Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		jq(".z-listbox-body").scrollTop(1000);
		waitResponse();
		int currentScroll = jq(".z-listbox-body").scrollTop();
		click(jq("@listcell:last .z-listitem-radio"));
		waitResponse();
		Assertions.assertEquals(currentScroll, jq(".z-listbox-body").scrollTop());
	}
}
