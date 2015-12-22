/* B80_ZK_2911Test.java

	Purpose:
		
	Description:
		
	History:
		2:55 PM 12/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2911Test extends WebDriverTestCase {
	@Test
	public void testZK2911() {
		connect();
		String text = jq("@label").text();
		assertEquals(
				"You should see each of the following [] containing with 1inside a component:def_value: [1]value2: [1]outside a vlayout:def_value: [1]value3: [1]",
				trim(text));
	}
}
