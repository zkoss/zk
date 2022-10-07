/* B96_ZK_5205Test.java

	Purpose:
		
	Description:
		
	History:
		11:10 AM 2022/10/7, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;



import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5205Test extends WebDriverTestCase {
	@Test
	public void testClientInfo() {
		connect();
		waitResponse();
		assertEquals(getZKLog(), "1.234567");
	}
}
