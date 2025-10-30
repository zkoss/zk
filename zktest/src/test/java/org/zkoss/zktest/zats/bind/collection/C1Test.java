/* C1Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 26 11:52:03 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.collection;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class C1Test extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		final ComponentAgent listbox = desktop.query("#contentListbox");
		Assertions.assertEquals(5, listbox.getChildren().size());

		final List<ComponentAgent> items = desktop.queryAll("#singleBox > comboitem");
		items.get(1).select();
		Assertions.assertEquals(9, listbox.getChildren().size());
	}
}
