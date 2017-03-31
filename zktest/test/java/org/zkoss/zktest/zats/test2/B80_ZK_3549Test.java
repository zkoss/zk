/** B80_ZK_3549Test.java.

	Purpose:

	Description:

	History:
 		Thu Mar 30 16:00:12 CST 2017, Created by christopher

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertEquals;

/**
 * @author christopher
 *
 */
public class B80_ZK_3549Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		assertEquals("Expecting same amount of children and childNodes",
				zk("@listbox").eval("$().ebodyrows.children.length", true),
				zk("@listbox").eval("$().ebodyrows.childNodes.length", true));
	}
}
