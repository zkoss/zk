/* B90_ZK_4417Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Nov 21 09:43:57 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B90_ZK_4417Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assert.assertEquals(getOffsetRightOf(jq("@datebox")), getOffsetRightOf(jq(".z-datebox-button")));
		Assert.assertEquals(getOffsetRightOf(jq("@spinner")), getOffsetRightOf(jq(".z-spinner-button")));
		Assert.assertEquals(getOffsetRightOf(jq("@combobox")), getOffsetRightOf(jq(".z-combobox-button")));
		Assert.assertEquals(getOffsetRightOf(jq("@bandbox")), getOffsetRightOf(jq(".z-bandbox-button")));
		Assert.assertEquals(getOffsetRightOf(jq("@timebox")), getOffsetRightOf(jq(".z-timebox-button")));
		Assert.assertEquals(getOffsetRightOf(jq("@doublespinner")), getOffsetRightOf(jq(".z-doublespinner-button")));
		Assert.assertEquals(getOffsetRightOf(jq("@timepicker")), getOffsetRightOf(jq(".z-timepicker-button")));
	}
	
	private int getOffsetRightOf(JQuery target) {
		return target.offsetLeft() + target.outerWidth();
	}
}
