/* B50_ZK_721Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 23 18:00:55 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_721Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button:first"));
		waitResponse();
		String iconHTML = jq(".z-tree-icon").html();
		click(jq("@button:last"));
		waitResponse();
		Assert.assertEquals(iconHTML, jq(".z-tree-icon").html());
	}
}
