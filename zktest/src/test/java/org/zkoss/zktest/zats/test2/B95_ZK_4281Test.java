/* B95_ZK_4281Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct 16 12:46:56 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Tree;

/**
 * @author rudyhuang
 */
public class B95_ZK_4281Test extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();

		desktop.query("#add").click();
		Assertions.assertEquals(2, desktop.queryAll("tree").size());

		final List<ComponentAgent> page2Btns = desktop.queryAll("button");
		page2Btns.forEach(ComponentAgent::click);
		Assertions.assertEquals(1, desktop.query("#testTree").as(Tree.class).getActivePage());
		Assertions.assertEquals(1, desktop.query("#treeCBS0").as(Tree.class).getActivePage());
	}
}
