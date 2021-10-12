/* F85_ZK_3711_1Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 27 14:10:06 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class F85_ZK_3711_1Test extends ZATSTestCase {
	@Test
	public void testHistoryPopStateMoreThanOne() throws Exception {
		try {
			connect();
			Assert.fail("Should throw an exception");
		} catch (Exception e) {
			String message = e.getMessage();
			if (!message.startsWith("more than one [@HistoryPopState]"))
				Assert.fail("Unknown exception: " + message);
		}
	}
}
