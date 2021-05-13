/* BasicPropertyBindingTest.java
	Purpose:

	Description:

	History:
		Thu May 06 18:24:49 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.propertybinding;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.bind.databinding.bean.Order;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

/**
 * @author jameschu
 */
public class ConditionalPropertyBindingTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		//[Step 1]
		ListModelList listModel = (ListModelList) desktopAgent.query("listbox").as(Listbox.class).getListModel();
		Order selected = ((Order) listModel.getSelection().iterator().next());
		ComponentAgent ib_quantityAgent = desktopAgent.query("#ib_quantity");
		Intbox ib_quantity = ib_quantityAgent.as(Intbox.class);
		ComponentAgent db_priceAgent = desktopAgent.query("#db_price");
		Doublebox db_price = db_priceAgent.as(Doublebox.class);
		assertEquals(selected.getQuantity(), ib_quantity.getValue().intValue());
		assertEquals(String.valueOf(selected.getPrice()), String.valueOf(db_price.getValue()));

		Label p1 = desktopAgent.query("#p1").as(Label.class);
		Label p2_1 = desktopAgent.query("#p2_1").as(Label.class);
		Label p2_2 = desktopAgent.query("#p2_2").as(Label.class);
		String initPrice = p1.getValue();
		assertEquals(String.valueOf(selected.getPrice()), initPrice);
		assertEquals(String.valueOf(selected.getPrice()), p2_1.getValue());
		assertEquals("-1.0", p2_2.getValue());
		Label p3 = desktopAgent.query("#p3").as(Label.class);
		Label p4 = desktopAgent.query("#p4").as(Label.class);
		Label p5 = desktopAgent.query("#p5").as(Label.class);
		Label p6 = desktopAgent.query("#p6").as(Label.class);
		assertEquals("", p3.getValue());
		assertEquals("", p4.getValue());
		assertEquals("", p5.getValue());
		assertEquals("", p6.getValue());

		//[Step 2]
		desktopAgent.query("#sel2Btn").click();
		selected = (Order) listModel.getSelection().iterator().next();
		assertEquals(selected.getQuantity(), ib_quantity.getValue().intValue());
		assertEquals(String.valueOf(selected.getPrice()), String.valueOf(db_price.getValue()));
		assertEquals(initPrice, p1.getValue());
		assertEquals(String.valueOf(selected.getPrice()), p2_1.getValue());
		assertEquals("-1.0", p2_2.getValue());
		assertEquals("", p3.getValue());
		assertEquals("", p4.getValue());
		assertEquals("", p5.getValue());
		assertEquals("", p6.getValue());

		//[Step 3]
		String lastPrice = p2_1.getValue();
		ib_quantityAgent.input(123);
		db_priceAgent.input(321);
		desktopAgent.query("#saveBtn").click();
		assertEquals(initPrice, p1.getValue());
		assertEquals(String.valueOf(selected.getPrice()), p2_1.getValue());
		assertEquals(lastPrice, p2_2.getValue());
		assertEquals("321.0", p3.getValue());
		assertEquals("", p4.getValue());
		assertEquals("321.0", p5.getValue());
		assertEquals("321.0", p6.getValue());

		//[Step 4]
		desktopAgent.query("#tb1").input("222");
		desktopAgent.query("#tb4").input("111");
		desktopAgent.query("#delBtn").click();
		assertEquals(null, ib_quantity.getValue());
		assertEquals(null, db_price.getValue());
		assertEquals(initPrice, p1.getValue());
		assertEquals("", p2_1.getValue());
		assertEquals("222.0", p2_2.getValue());
		assertEquals("321.0", p3.getValue());
		assertEquals("321.0", p4.getValue());
		assertEquals("", p5.getValue());
		assertEquals("222.0", p6.getValue());

		//[Step 5]
		desktopAgent.query("#sel0Btn").click();
		desktopAgent.query("#tb3").input("333");
		desktopAgent.query("#newBtn").click();
		assertEquals("0", String.valueOf(ib_quantity.getValue()));
		assertEquals("0.0", String.valueOf(db_price.getValue()));
		assertEquals(initPrice, p1.getValue());
		assertEquals("0.0", p2_1.getValue());
		assertEquals("333.0", p2_2.getValue());
		assertEquals("321.0", p3.getValue());
		assertEquals("321.0", p4.getValue());
		assertEquals("", p5.getValue());
		assertEquals("222.0", p6.getValue());
	}
}
