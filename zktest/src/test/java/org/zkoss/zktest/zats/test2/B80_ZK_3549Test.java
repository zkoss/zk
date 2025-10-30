/** B80_ZK_3549Test.java.

	Purpose:

	Description:

	History:
 		Thu Mar 30 16:00:12 CST 2017, Created by christopher

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;



/**
 * @author christopher
 *
 */
public class B80_ZK_3549Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		assertEquals(zk("@listbox").eval("$().ebodyrows.children.length", true),
				zk("@listbox").eval("$().ebodyrows.childNodes.length", true),
				"Expecting same amount of children and childNodes");
	}
}
