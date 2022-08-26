/* BasicPropertyBindingTest.java
	Purpose:

	Description:

	History:
		Thu May 06 18:24:49 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.propertybinding;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zktest.bind.databinding.bean.Order;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;

/**
 * @author jameschu
 */
public class CollectionPropertyBindingTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		//[Step 1]
		desktopAgent.query("#sel0Btn").click();
		Listbox listbox = desktopAgent.query("#lb").as(Listbox.class);
		ListModelList listModel = (ListModelList) listbox.getListModel();
		Order selected = ((Order) listModel.getSelection().iterator().next());
		ComponentAgent ib_quantityAgent = desktopAgent.query("#ib_quantity");
		Intbox ib_quantity = ib_quantityAgent.as(Intbox.class);
		ComponentAgent db_priceAgent = desktopAgent.query("#db_price");
		Doublebox db_price = db_priceAgent.as(Doublebox.class);
		assertEquals(selected.getQuantity(), ib_quantity.getValue().intValue());
		assertEquals(String.valueOf(selected.getPrice()), String.valueOf(db_price.getValue()));

		//[Step 2]
		desktopAgent.query("#sel2Btn").click();
		selected = (Order) listModel.getSelection().iterator().next();
		assertEquals(selected.getQuantity(), ib_quantity.getValue().intValue());
		assertEquals(String.valueOf(selected.getPrice()), String.valueOf(db_price.getValue()));

		//[Step 3]
		ib_quantityAgent.input(123);
		db_priceAgent.input(321);
		desktopAgent.query("#saveBtn").click();
		List<Component> listcells = listbox.getSelectedItem().getChildren();
		assertEquals("123", ((Listcell) listcells.get(2)).getLabel());
		assertEquals("321.0", ((Listcell) listcells.get(3)).getLabel());
		Grid grid = desktopAgent.query("#gd").as(Grid.class);
		assertEquals("123", ((Label) grid.getCell(2, 2)).getValue());
		assertEquals("321.0", ((Label) grid.getCell(2, 3)).getValue());

		//[Step 4]
		desktopAgent.query("#delBtn").click();
		assertEquals(null, ib_quantity.getValue());
		assertEquals(null, db_price.getValue());
		assertEquals(4, listbox.getItemCount());
		assertEquals(4, grid.getRows().getChildren().size());

		//[Step 5]
		desktopAgent.query("#newBtn").click();
		assertEquals("0", String.valueOf(ib_quantity.getValue()));
		assertEquals("0.0", String.valueOf(db_price.getValue()));
		assertEquals(5, listbox.getItemCount());
		assertEquals(5, grid.getRows().getChildren().size());
	}
}
