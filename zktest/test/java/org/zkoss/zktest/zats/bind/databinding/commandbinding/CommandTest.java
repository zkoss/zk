/* CommandTest.java
	Purpose:

	Description:

	History:
		Thu May 06 17:14:31 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.commandbinding;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class CommandTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		String[] blockIds = {"d1", "d2"};
		String[] resultTexts = {"newOrder", "saveOrder", "defaultAction", null, null};
		for (int i = 0; i < blockIds.length; i++) {
			List<ComponentAgent> btns = desktopAgent.queryAll("#" + blockIds[i] + " button");
			List<ComponentAgent> menuitems = desktopAgent.queryAll("#" + blockIds[i] + " menuitem");
			List<String> zkLog = desktopAgent.getZkLog();
			for (int j = 0; j < resultTexts.length; j++) {
				btns.get(j).click();
				menuitems.get(j).click();
				String resultText = resultTexts[j];
				if (resultText != null) {
					zkLog = desktopAgent.getZkLog();
					assertEquals(resultText, zkLog.get(zkLog.size() - 1));
					assertEquals(resultText, zkLog.get(zkLog.size() - 2));
				} else {
					assertEquals(zkLog.size(), desktopAgent.getZkLog().size());
				}
			}
		}
	}
}
