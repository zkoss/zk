/* B95_ZK_4585Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Aug 31 18:12:55 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B95_ZK_4585Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int defaultTop = jq(".z-checkbox-content").eq(0).positionTop();
		Assert.assertEquals("label of tristate mold is misaligned", defaultTop, jq(".z-checkbox-content").eq(1).positionTop(), 1);
		Assert.assertEquals("label of switch mold is misaligned", defaultTop, jq(".z-checkbox-content").eq(2).positionTop(), 1);
		Assert.assertEquals("label of toggle mold is misaligned", defaultTop, jq(".z-checkbox-content").eq(3).positionTop(), 1);
	}
}
