/* BasicTest.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 17:53:16 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.data;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;

/**
 * @author rudyhuang
 */
public class BasicTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect("/bind/viewmodel/data/basic.zul");
		Assert.assertEquals(7, desktop.query("#id").as(Intbox.class).getValue().intValue());
		Assert.assertEquals(399.99, desktop.query("#price").as(Doublebox.class).getValue(), 0.01);
		Assert.assertEquals("Potix", desktop.query("#name").as(Textbox.class).getValue());
		Assert.assertEquals("NY", desktop.query("#city").as(Textbox.class).getValue());
		Assert.assertEquals("7th Avenue", desktop.query("#street").as(Textbox.class).getValue());

		desktop.query("#id").type("1");
		desktop.query("#price").type("199.98");
		desktop.query("#name").type("ZK");
		desktop.query("#city").type("LA");
		desktop.query("#street").type("8th Street");
		desktop.query("#show").click();
		Assert.assertEquals("ID[1] Price[199.98] Name[ZK] Address[LA, 8th Street]", desktop.getZkLog().get(0));
	}
}
