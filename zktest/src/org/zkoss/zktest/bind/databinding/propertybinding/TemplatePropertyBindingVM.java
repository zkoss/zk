/* ConditionalPropertyBindingVM.java
	Purpose:

	Description:

	History:
		Fri May 07 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.propertybinding;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zktest.bind.databinding.bean.Order;
import org.zkoss.zktest.bind.databinding.service.FakeOrderService;
import org.zkoss.zktest.bind.databinding.service.OrderService;
import org.zkoss.zul.ListModelList;

/**
 * @author jameschu
 */
public class TemplatePropertyBindingVM {
	//the order list
	ListModelList<Order> orders;
	private String type = "customer";

	@Init
	public void init() {
		orders = new ListModelList<>(getService().list());
	}

	public ListModelList<Order> getOrders() {
		return orders;
	}

	@NotifyChange("type")
	@Command
	public void toggleType() {
		String customerType = "customer";
		if (customerType.equals(type))
			type = "admin";
		else
			type = customerType;
	}

	@Command
	@NotifyChange("orders")
	public void updateFirstOrderQuantity(@BindingParam("num") int num) {
		orders.get(0).setQuantity(num);
	}

	public String getType() {
		return type;
	}

	public OrderService getService() {
		return FakeOrderService.getInstance();
	}
}
