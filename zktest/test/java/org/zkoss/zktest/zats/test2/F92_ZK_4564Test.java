/* F92_ZK_4564Test.java

	Purpose:
		
	Description:
		
	History:
		Thu May 7 11:32:33 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class F92_ZK_4564Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertNoJSError();
		sleep(3000);
		Assert.assertEquals("loaded!", getZKLog());
		closeZKLog();
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("Clicked!", getZKLog());
	}
}
