/* F80_ZK_2642Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 29 10:53:28 CST 2015, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.ZATSTestCase;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author jameschu
 */
public class F80_ZK_2642Test extends ZATSTestCase {
	@Test
	public void test() {
		try {
			connect();
		} catch (Exception e) {
			assertTrue(e.getMessage().toString().contains("Root element <html> and DOCTYPE are not allowed in included file"));
			return;
		}
		fail();
	}
}
