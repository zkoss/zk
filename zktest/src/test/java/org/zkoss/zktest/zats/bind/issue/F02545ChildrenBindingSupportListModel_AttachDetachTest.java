/* F02545ChildrenBindingSupportListModel_AttachDetachTest.java
	Purpose:

	Description:

	History:
		Fri Apr 30 18:12:24 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class F02545ChildrenBindingSupportListModel_AttachDetachTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		ComponentAgent list1 = desktopAgent.query("window #list1");
		assertEquals(5, list1.getChildren().size());
		assertEquals(5, desktopAgent.query("window #list2").getChildren().size());

		desktopAgent.query("window #detachBtn").click();
		assertEquals(null, desktopAgent.query("window #list2"));

		desktopAgent.query("window #addBtn").click();
		assertEquals(7, list1.getChildren().size());

		desktopAgent.query("window #attachBtn").click();
		assertEquals(7, list1.getChildren().size());
		assertEquals(7, desktopAgent.query("window #list2").getChildren().size());
	}
}