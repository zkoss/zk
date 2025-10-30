/* BinderGlobalCommandTest.java
	Purpose:

	Description:

	History:
		Tue May 04 15:42:43 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.binder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class BinderGlobalCommandTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		String[][] btnIds = new String[][]{{"outerBtn1", "outerBtn2", "outerBtn3", "outerBtn4"},
				{"innerBtn1_1", "innerBtn1_2", "innerBtn1_3", "innerBtn1_4"},
				{"innerBtn2_1", "innerBtn2_2", "innerBtn2_3", "innerBtn2_4"},
				{"innerBtn3_1", "innerBtn3_2", "innerBtn3_3", "innerBtn3_4"},
				{"innerBtn4_1", "innerBtn4_2", "innerBtn4_3", "innerBtn4_4"}};
		for (int i = 0; i < btnIds.length; i++) {
			//[Step 1]
			desktopAgent.query("#" + btnIds[i][0]).click();
			List<String> zkLog = desktopAgent.getZkLog();
			assertEquals("GlobalCommand called > arg1: 1, arg2: outer", zkLog.get(zkLog.size() - 1));
			//[Step 2]
			desktopAgent.query("#" + btnIds[i][1]).click();
			assertEquals(zkLog.size(), desktopAgent.getZkLog().size());
			//[Step 3]
			desktopAgent.query("#" + btnIds[i][2]).click();
			zkLog = desktopAgent.getZkLog();
			assertEquals("GlobalCommand called > arg1: 3, arg2: inner", zkLog.get(zkLog.size() - 1));
			assertEquals("GlobalCommand called > arg1: 3, arg2: inner", zkLog.get(zkLog.size() - 2));
			//[Step 4]
			desktopAgent.query("#" + btnIds[i][3]).click();
			zkLog = desktopAgent.getZkLog();
			assertEquals("GlobalCommand called > arg1: 4, arg2: innerMyQueue", zkLog.get(zkLog.size() - 1));
			assertEquals("GlobalCommand called > arg1: 4, arg2: innerMyQueue", zkLog.get(zkLog.size() - 2));
		}
	}
}
