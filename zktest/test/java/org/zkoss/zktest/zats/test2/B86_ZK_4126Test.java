/* B86_ZK_4126Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 15:06:42 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4126Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq(".z-caption"));
		waitResponse(true);
		click(jq(".z-panel-expand"));
		waitResponse(true);
		Assert.assertFalse(isZKLogAvailable());
	}
}
