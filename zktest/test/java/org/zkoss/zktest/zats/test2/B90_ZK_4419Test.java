/* B90_ZK_4419Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Nov 28 14:53:06 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B90_ZK_4419Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery div = jq("@div");
		Assert.assertEquals(div.eq(0).outerWidth(), div.eq(1).outerWidth());
	}
}
