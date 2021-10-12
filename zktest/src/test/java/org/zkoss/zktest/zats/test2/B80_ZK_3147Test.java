/* B80_ZK_3062Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar 14 16:12:46 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author james
 */
public class B80_ZK_3147Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		waitResponse();
		assertEquals("true", getZKLog());
	}
}
