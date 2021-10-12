/* B70_ZK_3021Test.java

	Purpose:
		
	Description:
		
	History:
		5:28 PM 12/25/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 */
public class B70_ZK_3021Test extends ZATSTestCase {
	@Test
	public void testZK3021() {
		DesktopAgent agent = connect();
		List<ComponentAgent> labels = agent.queryAll("label");
		assertEquals(11, labels.size());
		for (int i = 1; i < 11; i += 2) {
			assertEquals(labels.get(i).as(Label.class).getValue(),
					labels.get(i + 1).as(Label.class).getValue());
		}
	}
}
