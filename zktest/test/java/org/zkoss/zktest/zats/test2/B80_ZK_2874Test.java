/* B80_ZK_2874Test.java

	Purpose:
		
	Description:
		
	History:
		3:08 PM 12/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2874Test extends WebDriverTestCase {
	@Test public void testZK2874() {
		connect();
		waitResponse(); // wait bookmark change
		assertEquals("You should see \"page 2\" below.page 2", trim(jq("@label").text()));
	}
}
