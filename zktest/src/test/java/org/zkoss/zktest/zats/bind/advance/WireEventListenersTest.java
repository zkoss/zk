/* WireEventListenersTest.java

		Purpose:
		
		Description:
		
		History:
				Thu May 06 15:30:06 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class WireEventListenersTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		desktop.query("button").click();

		Assert.assertArrayEquals(
			new String[] {
				"onClick"
			},
			desktop.getZkLog().toArray()
		);
	}
}
