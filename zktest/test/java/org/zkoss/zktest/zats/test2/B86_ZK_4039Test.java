/* B86_ZK_4039Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 27 12:37:42 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4039Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assert.assertFalse("Unexpected zklog", isZKLogAvailable());
	}
}
