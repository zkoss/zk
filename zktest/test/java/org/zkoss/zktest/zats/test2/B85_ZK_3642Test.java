/* B85_ZK_3642Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 11 17:57:37 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3642Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		try {
			connect();
		} catch (Throwable e) {
			Assert.fail("No exception allowed.");
		}
	}
}
