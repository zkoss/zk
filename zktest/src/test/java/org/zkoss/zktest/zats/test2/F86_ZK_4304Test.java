/* F86_ZK_4304Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 28 12:17:45 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class F86_ZK_4304Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertTrue(jq(".z-combobox-icon").hasClass("z-icon-caret-down"));
		Assert.assertTrue(jq(".z-bandbox-icon").hasClass("z-icon-search"));
		click(jq("@button").eq(0));
		waitResponse();
		Assert.assertTrue(jq(".z-combobox-icon").hasClass("z-icon-calendar"));
		Assert.assertTrue(jq(".z-bandbox-icon").hasClass("z-icon-calendar"));
		click(jq("@button").eq(1));
		waitResponse();
		Assert.assertTrue(jq(".z-combobox-icon").hasClass("z-icon-search"));
		Assert.assertTrue(jq(".z-bandbox-icon").hasClass("z-icon-search"));
	}
}
