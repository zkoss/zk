/* B90_ZK_4417Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Nov 21 09:43:57 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B90_ZK_4417Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assertions.assertEquals(getOffsetRightOf(jq("@datebox")), getOffsetRightOf(jq(".z-datebox-button")));
		Assertions.assertEquals(getOffsetRightOf(jq("@spinner")), getOffsetRightOf(jq(".z-spinner-button")));
		Assertions.assertEquals(getOffsetRightOf(jq("@combobox")), getOffsetRightOf(jq(".z-combobox-button")));
		Assertions.assertEquals(getOffsetRightOf(jq("@bandbox")), getOffsetRightOf(jq(".z-bandbox-button")));
		Assertions.assertEquals(getOffsetRightOf(jq("@timebox")), getOffsetRightOf(jq(".z-timebox-button")));
		Assertions.assertEquals(getOffsetRightOf(jq("@doublespinner")), getOffsetRightOf(jq(".z-doublespinner-button")));
		Assertions.assertEquals(getOffsetRightOf(jq("@timepicker")), getOffsetRightOf(jq(".z-timepicker-button")));
	}
	
	private int getOffsetRightOf(JQuery target) {
		return target.offsetLeft() + target.outerWidth();
	}
}
