/* B80_ZK_2895Test.java

	Purpose:
		
	Description:
		
	History:
		2:53 PM 12/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2895Test extends ZATSTestCase {
	@Test
	public void testZK2895() {
		try {
			connect();
		} catch (Exception e) {
			fail();
		}
	}
}
