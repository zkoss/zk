/* B70_ZK_2857Test.java

	Purpose:
		
	Description:
		
	History:
		4:15 PM 9/2/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B70_ZK_2857Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		try {
			List<ComponentAgent> labels = desktop.queryAll("label");

			assertEquals(2, labels.size());
			assertEquals("Europe/Berlin", labels.get(1).as(Label.class).getValue());

		} catch (Exception e) {
			assertFalse(true);
		}
	}
}
