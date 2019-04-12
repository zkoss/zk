/* B50_ZK_286Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 03 15:59:36 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B50_ZK_286Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertEquals("false", getZKLog());
	}
}
