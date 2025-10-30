/* InitTest.java
	Purpose:

	Description:

	History:
		Thu May 06 16:43:47 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.initialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class InitTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		String[] blockIds = {"d1", "d2"};
		for (int i = 0; i < blockIds.length; i++) {
			List<ComponentAgent> labels = desktopAgent.queryAll("#" + blockIds[i] + " label");
			assertEquals("test", labels.get(0).as(Label.class).getValue());
			assertEquals("123", labels.get(1).as(Label.class).getValue());
			assertEquals("test", labels.get(2).as(Label.class).getValue());
			assertTrue(desktopAgent.query("#" + blockIds[i] + " checkbox").as(Checkbox.class).isChecked());
		}
	}
}
