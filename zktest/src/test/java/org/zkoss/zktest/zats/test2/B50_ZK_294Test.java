/* B50_ZK_294Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 03 16:27:23 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_294Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assert.assertEquals("This shall not have <span>", jq("span").eq(1).text().trim());
		Assert.assertEquals("<span> inside textarea", jq("textarea").eq(0).text().trim());
	}
}
