/* F104_ZK_6097_BreadcrumbComposer.java

	Purpose:

	Description:

	History:
		Wed May 13 13:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Breadcrumb;
import org.zkoss.zul.Breadcrumbitem;
import org.zkoss.zul.Label;

public class F104_ZK_6097_BreadcrumbComposer extends SelectorComposer<Component> {

	@Wire
	private Breadcrumb bcMvc;
	@Wire
	private Label mvcSeparator;

	@Listen("onClick = #btnPipeMvc")
	public void usePipe() {
		bcMvc.setSeparator("|");
		mvcSeparator.setValue(bcMvc.getSeparator());
	}

	@Listen("onClick = #btnSlashMvc")
	public void useSlash() {
		bcMvc.setSeparator("/");
		mvcSeparator.setValue(bcMvc.getSeparator());
	}

	@Listen("onClick = #btnAddItem")
	public void addItem() {
		Breadcrumbitem item = new Breadcrumbitem();
		item.setLabel("New Page");
		bcMvc.appendChild(item);
	}

	@Listen("onClick = #btnSetMaxItems")
	public void setMaxItems() {
		bcMvc.setMaxItems(3);
	}
}
