/* ConditionalPropertyBindingVM.java
	Purpose:

	Description:

	History:
		Fri May 07 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.databinding.propertybinding;

import java.util.List;

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
public class CollectionPropertyBindingVM {
	//the order list
	List<Order> orders;

	//the selected order
	private Order selected;

	@Init
	public void init() {
		orders = new ListModelList<>(getService().list());
	}

	//action command
	@NotifyChange({"selected", "orders"})
	@Command
	public void newOrder() {
		Order order = new Order();
		getOrders().add(order);
		selected = order;//select the new one
	}

	@NotifyChange("selected")
	@Command
	public void saveOrder() {
		selected.setPrice(Math.round(selected.getPrice()));
		getService().save(selected);
	}

	@NotifyChange({"selected", "orders"})
	@Command
	public void deleteOrder() {
		getService().delete(selected);//delete selected
		getOrders().remove(selected);
		selected = null; //clean the selected
	}

	@NotifyChange("selected")
	@Command
	public void selOrder0() {
		selected = orders.get(0);
	}

	@NotifyChange("selected")
	@Command
	public void selOrder2() {
		selected = orders.get(2);
	}

	public List<Order> getOrders() {
		return orders;
	}

	public Order getSelected() {
		return selected;
	}

	public void setSelected(Order selected) {
		this.selected = selected;
	}

	public OrderService getService() {
		return FakeOrderService.getInstance();
	}
}
