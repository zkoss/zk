/* B86_ZK_4081Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Nov 20 16:39:54 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4081Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int spans = jq("@div > .z-label").length();
		for (int i = 0; i < spans; i++) {
			if (i == spans - 1) {
				Assert.assertEquals("true", jq("@div > .z-label").eq(i).text());
			} else {
				Assert.assertEquals("false", jq("@div > .z-label").eq(i).text());
			}
		}
	}
}
