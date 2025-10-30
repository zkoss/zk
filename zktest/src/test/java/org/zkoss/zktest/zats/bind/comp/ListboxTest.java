/* ListboxTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 12:28:08 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.OpenAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class ListboxTest extends ZATSTestCase {
	@Test
	public void testListgroup() {
		final DesktopAgent desktop = connect();
		final OpenAgent listgroup = desktop.query("listgroup").as(OpenAgent.class);
		final Label open = desktop.query("#open").as(Label.class);

		listgroup.open(true);
		Assertions.assertEquals("true", open.getValue());
		listgroup.open(false);
		Assertions.assertEquals("false", open.getValue());
	}

	@Test
	public void testSelected() {
		final DesktopAgent desktop = connect();
		final Label selectedItem = desktop.query("#itemLabel").as(Label.class);
		final Label selectedIndex = desktop.query("#indexLabel").as(Label.class);

		desktop.queryAll("#listbox > listitem").get(0).select();
		Assertions.assertEquals("item01", selectedItem.getValue());
		Assertions.assertEquals("0", selectedIndex.getValue());

		desktop.queryAll("#listbox > listitem").get(3).select();
		Assertions.assertEquals("item04", selectedItem.getValue());
		Assertions.assertEquals("3", selectedIndex.getValue());
	}
}
