/* CollectionsTest.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 14:53:48 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.data;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class CollectionsTest extends ZATSTestCase {
	private DesktopAgent desktop;

	@Before
	public void setUp() {
		desktop = connect("/bind/viewmodel/data/collections.zul");
	}

	@Test
	public void testRemove() {
		desktop.queryAll("#gL row").get(0).queryAll("button").get(0).click();
		desktop.queryAll("#gS row").get(0).queryAll("button").get(0).click();
		desktop.queryAll("#gM row").get(0).queryAll("button").get(0).click();
		Assert.assertEquals(1, desktop.queryAll("#gL row").size());
		Assert.assertEquals(1, desktop.queryAll("#gS row").size());
		Assert.assertEquals(1, desktop.queryAll("#gM row").size());
	}

	@Test
	public void testAdd() {
		desktop.query("#addL").click();
		desktop.query("#addS").click();
		desktop.query("#addM").click();
		Assert.assertEquals(3, desktop.queryAll("#gL row").size());
		Assert.assertEquals(3, desktop.queryAll("#gS row").size());
		Assert.assertEquals(3, desktop.queryAll("#gM row").size());
	}

	@Test
	public void testInplaceEdit() {
		inplaceEdit(desktop.queryAll("#gL row").get(0));
		inplaceEdit(desktop.queryAll("#gS row").get(0));
		inplaceEdit(desktop.queryAll("#gM row").get(0));
		Assert.assertEquals("ZK, 9th Street", desktop.getZkLog().get(0));
		Assert.assertEquals("ZK, 9th Street", desktop.getZkLog().get(1));
		Assert.assertEquals("ZK, 9th Street", desktop.getZkLog().get(2));
	}

	private void inplaceEdit(ComponentAgent item) {
		final List<ComponentAgent> textboxes = item.queryAll("textbox");
		textboxes.get(0).type("ZK");
		textboxes.get(1).type("9th Street");
		item.queryAll("button").get(1).click();
	}
}
