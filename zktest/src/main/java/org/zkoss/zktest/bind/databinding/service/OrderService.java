package org.zkoss.zktest.bind.databinding.service;
/* SearchService.java

	Purpose:
		
	Description:
		
	History:
		2011/10/25 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */


import java.util.List;

import org.zkoss.zktest.bind.databinding.bean.Order;

/**
 * @author dennis
 */
public interface OrderService {
	public List<Order> list();

	public void save(Order order);

	public void delete(Order order);
}
