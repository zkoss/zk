/* B80_ZK_3062Test.java

	Purpose:
		
	Description:
		
	History:
		12:11 PM 1/11/16, Created by jumperchen

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_3062Test extends WebDriverTestCase {
	@Test public void testZK3062() {
		connect();
		waitResponse();
		assertEquals("true", getZKLog());
	}
}
