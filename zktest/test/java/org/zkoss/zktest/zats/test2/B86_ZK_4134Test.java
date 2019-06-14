/* B86_ZK_4134Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 15:48:36 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4134Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertEquals(2, jq(".z-chosenbox-item").length());
		sleep(6500);
		Assert.assertEquals(0, jq(".z-chosenbox-item").length());
	}
}
