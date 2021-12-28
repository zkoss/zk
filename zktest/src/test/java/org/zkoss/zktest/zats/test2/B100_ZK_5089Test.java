/* B100_ZK_5089Test.java

	Purpose:
		
	Description:
		
	History:
		3:36 PM 2021/12/28, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B100_ZK_5089Test extends WebDriverTestCase {
	@Test
	public void testAfterSize() {
		connect();
		waitResponse();
		String label = jq("$lb1").text();
		assertNotNull(label);
		assertTrue(label.contains("width: 300"));
		assertTrue(label.contains("height: 300"));
	}
}
