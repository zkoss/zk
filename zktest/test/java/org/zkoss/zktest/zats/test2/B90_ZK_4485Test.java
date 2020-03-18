/* B90_ZK_4485Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 13 10:03:20 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B90_ZK_4485Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		Assert.assertEquals("true", getZKLog());
	}
}
