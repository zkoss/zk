/* B85_ZK_3829Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 11:34:35 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B85_ZK_3829Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertEquals(1, jq(".z-label[style*=\"color: red\"]:contains(fails2)").length());
		Assert.assertEquals(2, jq(".z-label[style*=\"color: red\"]:contains(fails)").length());
	}
}
